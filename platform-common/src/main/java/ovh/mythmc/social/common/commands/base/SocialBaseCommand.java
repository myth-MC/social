package ovh.mythmc.social.common.commands.base;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissionDefault;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.Social.ReloadType;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext.HeaderType;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.impl.EmojiDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.HistoryMenu;
import ovh.mythmc.social.common.gui.impl.KeywordDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.PlayerInfoMenu;

@Command("social")
public final class SocialBaseCommand {

    @Command("announcement")
    @Permission("social.use.announcement")
    @Flag(flag = "s", longFlag = "self")
    public void announce(SocialUser user, @Suggestion("announcements") Integer id, Flags flags) {
        SocialAnnouncement announcement = Social.get().getAnnouncementManager().getAnnouncements().get(id);

        if (Social.get().getConfig().getSettings().getAnnouncements().isUseActionBar()) {
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
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserMuted(), target.getNickname());
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
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserMutedGlobally(), target.getNickname());
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, target.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getUserMutedGlobally());
        }
    }

    @Command("unmute")
    @Permission(value = "social.use.unmute", def = PermissionDefault.OP)
    public void unmute(SocialUser user, SocialUser target, @Optional ChatChannel channel) {
        if (channel != null) { // unmute in channel
            if (!Social.get().getUserManager().isMuted(target, channel)) {
                Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getUserIsNotMuted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getUserManager().unmute(target, channel);

            // Command sender
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserUnmuted(), target.getNickname());
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
            String successMessage = String.format(Social.get().getConfig().getMessages().getCommands().getUserUnmutedGlobally(), target.getNickname());
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), successMessage, Social.get().getConfig().getMessages().getChannelType());

            // Target
            Social.get().getTextProcessor().parseAndSend(target, target.getMainChannel(), Social.get().getConfig().getMessages().getInfo().getUserUnmutedGlobally(), Social.get().getConfig().getMessages().getChannelType());
        }
    }

    @Command("nickname")
    @Permission(value = "social.use.nickname", def = PermissionDefault.TRUE)
    public class Nickname {

        @Command
        public void nickname(SocialUser user, String nickname, @Optional SocialUser target) {
            if (nickname.length() > 16) {
                Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNicknameTooLong(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            // Check no one has this nickname
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null &&
                        offlinePlayer.getName().equalsIgnoreCase(nickname) &&
                        offlinePlayer.hasPlayedBefore()) {
                    Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNicknameAlreadyInUse(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }
            }

            if (target != null) {
                if (nickname.equalsIgnoreCase("reset"))
                    nickname = target.player().get().getName();

                if (!user.player().get().hasPermission("social.use.nickname.others")) {
                    Social.get().getTextProcessor().parseAndSend(user, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                target.player().get().setDisplayName(nickname);
                Social.get().getTextProcessor().parseAndSend(user, String.format(Social.get().getConfig().getMessages().getCommands().getNicknameChangedOthers(), target.player().get().getName(), nickname), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            if (nickname.equalsIgnoreCase("reset"))
                nickname = user.player().get().getName();

            user.player().get().setDisplayName(nickname);
            Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getCommands().getNicknameChanged(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

    }

    @Command("parse")
    @Permission("social.use.parse")
    @Flag(flag = "u", longFlag = "user", argument = SocialUser.class)
    @Flag(flag = "c", longFlag = "channel", argument = ChatChannel.class)
    @Flag(flag = "t", longFlag = "channelType", argument = ChannelType.class)
    @Flag(flag = "p", longFlag = "playerInput", argument = boolean.class)
    public void parse(SocialUser user, @Suggestion("placeholders") Flags flags) {
        String message = flags.getText();
        
        SocialParserContext context = SocialParserContext.builder(flags.getFlagValue("u", SocialUser.class).orElse(user), Component.text(message))
            .channel(flags.getFlagValue("c", ChatChannel.class).orElse(user.getMainChannel()))
            .messageChannelType(flags.getFlagValue("t", ChannelType.class).orElse(ChannelType.CHAT))
            .build();

        user.sendParsableMessage(context, flags.getFlagValue("p", boolean.class).orElse(false));
    }

    @Command("reload")
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
    @Permission("social.use.socialspy")
    public void socialSpy(SocialUser user) {
        Social.get().getUserManager().setSocialSpy(user, !user.isSocialSpy());
        Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), Social.get().getConfig().getMessages().getCommands().getSocialSpyStatusChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

}
