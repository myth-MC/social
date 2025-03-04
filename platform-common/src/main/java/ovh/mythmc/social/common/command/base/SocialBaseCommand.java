package ovh.mythmc.social.common.command.base;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.Social.ReloadType;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.formatter.SocialFormatter;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext.HeaderType;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.impl.EmojiDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.HistoryMenu;
import ovh.mythmc.social.common.gui.impl.KeywordDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.PlayerInfoMenu;

@Command("social")
@Description("Main command for social")
@Syntax("/social test")
public final class SocialBaseCommand {

    @Command("announce")
    @Description("Announces a provided parsable message")
    @Permission(value = "social.use.announce", def = PermissionDefault.OP)
    @Flag(flag = "s", longFlag = "self", description = "Shows this message only to the sender")
    @Flag(flag = "t", longFlag = "channelType", argument = ChannelType.class, description = "The channel type of this message")
    public void announce(SocialUser user, Flags flags) {
        final String message = flags.getText();
        final boolean self = flags.hasFlag("s");
        final var channelType = flags.getFlagValue("t", ChannelType.class).orElse(ChannelType.CHAT);

        final var context = SocialParserContext.builder(user, Component.text(message))
            .messageChannelType(channelType)
            .build();

        if (self) {
            user.sendParsableMessage(context);
            return;
        }

        Social.get().getUserManager().get().forEach(recipient -> recipient.sendParsableMessage(context.withChannel(recipient.getMainChannel())));
    }

    @Command("announcement")
    @Description("Announces a configured announcement")
    @Permission(value = "social.use.announcement", def = PermissionDefault.OP)
    @Flag(flag = "s", longFlag = "self", description = "Shows this message only to the sender")
    public void announce(SocialUser user, @Suggestion("announcements") Integer id, Flags flags) {
        final int announcementsSize = Social.get().getAnnouncementManager().getAnnouncements().size();
        if (id > announcementsSize || id < 0) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getUnknownAnnouncement(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialAnnouncement announcement = Social.get().getAnnouncementManager().getAnnouncements().get(id);
        if (announcement == null) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getUnknownAnnouncement(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (Social.get().getConfig().getAnnouncements().isUseActionBar()) {
            if (flags.hasFlag("s")) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), announcement.message(), ChannelType.ACTION_BAR);
                return;
            }

            Social.get().getUserManager().get().forEach(s -> Social.get().getTextProcessor().parseAndSend(s, s.getMainChannel(), announcement.message(), ChannelType.ACTION_BAR));
        } else {
            if (flags.hasFlag("s")) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), announcement.message(), ChannelType.CHAT);
                return;
            }

            for (ChatChannel channel : announcement.channels()) {
                channel.getMembers().forEach(s -> {
                    Social.get().getTextProcessor().parseAndSend(s, s.getMainChannel(), announcement.message(), ChannelType.CHAT);
                });
            }
        }
    }

    @Command("channel")
    @Description("Switches your main channel")
    @Permission(value = "social.use.channel", def = PermissionDefault.TRUE)
    public void channel(SocialUser user, ChatChannel channel) {
        if (channel instanceof GroupChatChannel && !channel.getMemberUuids().contains(user.getUuid())) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (channel == user.getMainChannel())
            return;

        if (channel.getPermission() != null && !user.player().get().hasPermission(channel.getPermission())) {
            Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getUserManager().setMainChannel(user, channel);
    }

    @Command("dictionary")
    @Description("Opens the dictionary")
    @Permission(value = "social.use.dictionary", def = PermissionDefault.TRUE)
    public class Dictionary {

        @Command
        public void execute(SocialUser user, Type type) {
            switch (type) {
                case EMOJIS: {
                    SocialMenuContext context = SocialMenuContext.builder()
                        .viewer(user)
                        .build();

                    EmojiDictionaryMenu dictionary = new EmojiDictionaryMenu();
                    dictionary.open(context);
                    break;
                }
                case KEYWORDS: {
                    SocialMenuContext context = SocialMenuContext.builder()
                        .viewer(user)
                        .build();

                    KeywordDictionaryMenu dictionary = new KeywordDictionaryMenu();
                    dictionary.open(context);
                    break;
                }
            }
        }

        enum Type {
            EMOJIS,
            KEYWORDS
        }
    }

    @Command("history")
    @Description("Opens the chat history")
    @Permission("social.use.history")
    public class History {

        @Command
        public void global(SocialUser user) {
            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .messages(Social.get().getChatManager().getHistory().get())
                .viewer(user)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        @Command("channel")
        public void channel(SocialUser user, ChatChannel channel) {
            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .headerType(HeaderType.CHANNEL)
                .messages(Social.get().getChatManager().getHistory().getByChannel(channel))
                .viewer(user)
                .channel(channel)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        @Command("thread")
        public void thread(SocialUser user, int threadId) {
            SocialRegisteredMessageContext message = Social.get().getChatManager().getHistory().getById(threadId);
            if (message == null) {
                // message does not exist
                return;
            }

            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .headerType(HeaderType.THREAD)
                .messages(Social.get().getChatManager().getHistory().getThread(message, 128))
                .viewer(user)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
            return;
        }

        @Command("player")
        public void player(SocialUser user, SocialUser target) {
            SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
                .headerType(HeaderType.PLAYER)
                .messages(Social.get().getChatManager().getHistory().getByUser(target))
                .viewer(user)
                .target(target)
                .build();

            HistoryMenu history = new HistoryMenu();
            history.open(context);
        }
    }
    
    @Command("info")
    @Description("Opens the user information menu")
    @Permission("social.use.info")
    public void self(SocialUser user, @Optional SocialUser target) {
        SocialMenuContext context = SocialMenuContext.builder()
            .viewer(user)
            .target(java.util.Optional.ofNullable(target).orElse(user))
            .build();

        PlayerInfoMenu playerInfo = new PlayerInfoMenu();
        playerInfo.open(context);
    }

    @Command("mute")
    @Description("Mutes a user")
    @Permission(value = "social.use.mute", def = PermissionDefault.OP)
    public void mute(SocialUser user, SocialUser target, @Optional ChatChannel channel) {
        if (target.equals(user)) {
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getCannotMuteYourself(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (target.player().isPresent() && target.player().get().hasPermission("social.mute.excempt")) {
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserExcemptFromMute(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (channel != null) { // mute
            if (Social.get().getUserManager().isMuted(target, channel)) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsAlreadyMuted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getUserManager().mute(target, channel);

            // Command sender
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserMuted(), target.getCachedDisplayName());
            Social.get().getTextProcessor().parseAndSend(user, channel, successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, channel, Social.get().getConfig().getMessages().getInfo().getUserMuted(), Social.get().getConfig().getMessages().getChannelType());
        } else { // global mute
            if (Social.get().getUserManager().isGloballyMuted(target)) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsAlreadyMuted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getChatManager().getChannels().forEach(registeredChannel -> Social.get().getUserManager().mute(target, registeredChannel));

            // Command sender
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserMutedGlobally(), target.getCachedDisplayName());
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, target.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getUserMutedGlobally());
        }
    }

    @Command("unmute")
    @Description("Unmutes a user")
    @Permission(value = "social.use.unmute", def = PermissionDefault.OP)
    public void unmute(SocialUser user, SocialUser target, @Optional ChatChannel channel) {
        if (channel != null) { // unmute in channel
            if (!Social.get().getUserManager().isMuted(target, channel)) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsNotMuted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getUserManager().unmute(target, channel);

            // Command sender
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserUnmuted(), target.getCachedDisplayName());
            Social.get().getTextProcessor().parseAndSend(user, channel, successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, channel, Social.get().getConfig().getMessages().getInfo().getUserUnmuted(), Social.get().getConfig().getMessages().getChannelType());
        } else { // unmute globally
            if (!Social.get().getUserManager().isGloballyMuted(target)) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsNotMuted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getChatManager().getChannels().forEach(registeredChannel -> Social.get().getUserManager().unmute(target, registeredChannel));

            // Command sender
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserUnmutedGlobally(), target.displayName());
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, target.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getUserUnmutedGlobally(), Social.get().getConfig().getMessages().getChannelType());
        }
    }

    @Command("nickname")
    public final class Nickname {

        @Command("set")
        @Description("Sets a nickname")
        @Permission(value = "social.use.nickname.set", def = PermissionDefault.TRUE)
        public void set(SocialUser user, String nickname, @Optional SocialUser target) {
            if (nickname.length() > 16) {
                Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNicknameTooLong(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            // Check no one has this nickname
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null &&
                        offlinePlayer.getName() != user.player().get().getName() &&
                        offlinePlayer.getName().equalsIgnoreCase(nickname) &&
                        offlinePlayer.hasPlayedBefore()) {
                    Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNicknameAlreadyInUse(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }
            }

            if (target != null) { // Other user's nickname
                if (nickname.equalsIgnoreCase("reset"))
                    nickname = target.player().get().getName();
    
                if (!user.player().get().hasPermission("social.use.nickname.set.others")) {
                    Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }
    
                Social.get().getUserManager().setDisplayName(target, nickname);
                Social.get().getTextProcessor().parseAndSend(user, String.format(Social.get().getConfig().getMessages().getCommands().getNicknameChangedOthers(), target.player().get().getName(), nickname), Social.get().getConfig().getMessages().getChannelType());
            } else { // Sender's nickname
                if (nickname.equalsIgnoreCase("reset"))
                    nickname = user.player().get().getName();

                Social.get().getUserManager().setDisplayName(user, nickname);
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getCommands().getNicknameChanged(), Social.get().getConfig().getMessages().getChannelType());
            }
        }

        @Command("color")
        @Description("Sets your nickname color")
        @Permission(value = "social.use.nickname.color", def = PermissionDefault.OP)
        public void color(SocialUser user, TextColor color, @Optional SocialUser target) {
            if (color.asHexString().contains("dbdbdb"))
                color = null;

            if (target != null) { // Other user's nickname color
                if (!user.player().get().hasPermission("social.use.nickname.color.others")) {
                    Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                Social.get().getUserManager().setDisplayNameStyle(target, Style.style(color));
                Social.get().getTextProcessor().parseAndSend(user, String.format(Social.get().getConfig().getMessages().getCommands().getNicknameChangedOthers(), target.player().get().getName(), target.getCachedDisplayName()), Social.get().getConfig().getMessages().getChannelType());
            } else { // Sender's nickname color
                Social.get().getUserManager().setDisplayNameStyle(user, Style.style(color));
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getCommands().getNicknameChanged(), Social.get().getConfig().getMessages().getChannelType());
            }
        }

    }

    @Command("processor")
    @Description("Accesses social's built-in text processor")
    @Permission(value = "social.use.processor", def = PermissionDefault.OP)
    public final class Processor {

        @Command("info")
        @Description("Shows information about the text processor")
        @Permission("social.use.processor.info")
        public void info(SocialUser user) {
            final String registeredParsers = String.format(
                Social.get().getConfig().getMessages().getCommands().getProcessorInfoParsers(),
                Social.get().getTextProcessor().getContextualParsers().size(),
                Social.get().getTextProcessor().getContextualParsersByType(SocialParserGroup.class).size());

            user.sendParsableMessage(registeredParsers);
            user.sendParsableMessage(getFormattedParserInfo(SocialContextualPlaceholder.class));
            user.sendParsableMessage(getFormattedParserInfo(SocialContextualKeyword.class));
            user.sendParsableMessage(getFormattedParserInfo(SocialFormatter.class));
        }

        @Command("parse")
        @Description("Parses a message and returns it")
        @Permission("social.use.processor.parse")
        @Flag(flag = "u", longFlag = "user", argument = SocialUser.class, description = "User for the parser context")
        @Flag(flag = "c", longFlag = "channel", argument = ChatChannel.class, description = "Channel for the parser context")
        @Flag(flag = "t", longFlag = "channelType", argument = ChannelType.class, description = "Channel type for the parser context")
        @Flag(flag = "i", longFlag = "userInput", argument = boolean.class, description = "Whether to treat this message as user's input")
        public void parse(SocialUser user, Flags flags) {
            final String input = flags.getText();

            final var context = SocialParserContext.builder(flags.getFlagValue("u", SocialUser.class).orElse(user), Component.text(input))
                .channel(flags.getFlagValue("c", ChatChannel.class).orElse(user.getMainChannel()))
                .messageChannelType(flags.getFlagValue("t", ChannelType.class).orElse(ChannelType.CHAT))
                .build();
    
            final var parsedInput = Social.get().getTextProcessor().parse(context);
            if (context.messageChannelType().equals(ChannelType.ACTION_BAR)) {
                user.sendParsableMessage(context, flags.getFlagValue("i", boolean.class).orElse(false));
                return;
            }

            final var resultMessage = Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                .append(parsedInput)
                .hoverEvent(Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorClickToAnnounce()))
                .clickEvent(ClickEvent.callback(audience -> {
                    Social.get().getUserManager().get().forEach(recipient -> recipient.sendMessage(parsedInput));
                }));

            user.sendParsableMessage(context.withMessage(resultMessage), flags.getFlagValue("p", boolean.class).orElse(false));
        }

        @Command("get")
        @Description("Gets a specific parser's result")
        @Permission(value = "social.use.processor.get", def = PermissionDefault.OP)
        public final class Get {

            @Command("placeholder")
            @Permission(value = "social.use.processor.get.placeholder", def = PermissionDefault.OP)
            @Flag(flag = "u", longFlag = "user", argument = SocialUser.class, description = "User for the parser's context")
            public void placeholder(SocialUser user, SocialContextualPlaceholder placeholder, Flags flags) {
                final var target = flags.getFlagValue("u", SocialUser.class).orElse(user);
                var context = SocialParserContext.builder(target, Component.empty()).build();
                final var optionalGroup = Social.get().getTextProcessor().getGroupByContextualParser(placeholder.getClass());

                if (optionalGroup.isPresent())
                    context = context.withGroup(java.util.Optional.of(optionalGroup.get()));

                user.sendParsableMessage(
                    Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                        .append(placeholder.get(context)));
            }

            @Command("keyword")
            @Permission(value = "social.use.processor.get.keyword", def = PermissionDefault.OP)
            @Flag(flag = "u", longFlag = "user", argument = SocialUser.class, description = "User for the parser's context")
            public void keyword(SocialUser user, SocialContextualKeyword keyword, Flags flags) {
                final var target = flags.getFlagValue("u", SocialUser.class).orElse(user);
                var context = SocialParserContext.builder(target, Component.empty()).build();
                final var optionalGroup = Social.get().getTextProcessor().getGroupByContextualParser(keyword.getClass());

                if (optionalGroup.isPresent())
                    context = context.withGroup(java.util.Optional.of(optionalGroup.get()));
                
                    user.sendParsableMessage(
                        Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                            .append(keyword.process(context)));
            }

        }

        private static Component getFormattedParserInfo(Class<? extends SocialContextualParser> type) {
            final var typeParsers = Social.get().getTextProcessor().getContextualParsersByType(type);
            final String message =  String.format(Social.get().getConfig().getMessages().getCommands().getProcessorInfoParsersByType(), 
                type.getSimpleName(),
                typeParsers.size());

            return Component.text(message)
                .hoverEvent(Component.text(typeParsers.stream().map(p -> p.getClass().getSimpleName()).toList().toString(), NamedTextColor.GRAY));
        }

    }

    @Command("reload")
    @Description("Reloads the plugin")
    @Permission("social.use.reload")
    public void reload(SocialUser user, @Optional ReloadType type) {
        type = java.util.Optional.ofNullable(type).orElse(ReloadType.ALL);

        Social.get().reload(type);
        Social.get().getTextProcessor().parseAndSend(
            user,
            String.format(Social.get().getConfig().getMessages().getCommands().getModuleReloaded(), type),
            Social.get().getConfig().getMessages().getChannelType()
        );
    }

    @Command("socialspy")
    @Description("Toggles the socialspy status")
    @Permission("social.use.socialspy")
    public void socialSpy(SocialUser user) {
        Social.get().getUserManager().setSocialSpy(user, !user.isSocialSpy());
        Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getCommands().getSocialSpyStatusChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

}
