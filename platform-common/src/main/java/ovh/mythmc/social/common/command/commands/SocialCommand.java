package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.Social.ReloadType;
import ovh.mythmc.social.api.announcements.Announcement;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.user.InGameSocialUser;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.command.MainCommand;
import ovh.mythmc.social.common.command.parser.ChannelParser;
import ovh.mythmc.social.common.command.parser.IdentifiedParserParser;
import ovh.mythmc.social.common.command.parser.UserParser;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.impl.EmojiDictionaryMenu;
import ovh.mythmc.social.common.gui.impl.KeywordDictionaryMenu;

public final class SocialCommand implements MainCommand<SocialUser> {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public void register(@NotNull CommandManager<SocialUser> commandManager) {
        final Command.Builder<SocialUser> socialCommand = commandManager.commandBuilder("social");

        MinecraftHelp<SocialUser> help = MinecraftHelp.createNative("/social help", commandManager);
        commandManager.command(socialCommand
                .literal("help", "?")
                .commandDescription(Description.of("Shows information about /social and its subcommands"))
                .optional("query", StringParser.greedyStringParser(), DefaultValue.constant(""))
                .handler(ctx -> {
                    help.queryCommands(ctx.get("query"), ctx.sender());
                })
        );

        // /social announce
        commandManager.command(socialCommand
                .literal("announce")
                .commandDescription(Description.of("Announces a provided parsable message"))
                .permission("social.use.announce")
                .required("message", StringParser.greedyFlagYieldingStringParser())
                .flag(commandManager.flagBuilder("self")
                        .withDescription(Description.of("Shows this message to the sender only")))
                .flag(commandManager.flagBuilder("type")
                        .withDescription(Description.of("The channel type of this message"))
                        .withComponent(EnumParser.enumParser(ChatChannel.ChannelType.class)))
                .handler(ctx -> {
                    final boolean self = ctx.flags().isPresent("self");
                    final String message = ctx.get("message");

                    final var contextBuilder = SocialParserContext.builder(ctx.sender(), Component.text(message));
                    if (ctx.flags().isPresent("type"))
                        contextBuilder.messageChannelType(ctx.flags().get("type"));

                    if (self) {
                        ctx.sender().sendParsableMessage(contextBuilder.build());
                        return;
                    }

                    Social.get().getUserService().get()
                            .forEach(recipient -> recipient.sendParsableMessage(contextBuilder.build()));
                })
                .build());

        // /social announcement
        commandManager.command(socialCommand
                .literal("announcement")
                .commandDescription(Description.of("Announces a configured message"))
                .permission("social.use.announcement")
                .required("id",
                        IntegerParser.integerParser(0, Social.registries().announcements().registry().size() - 1), Description.of("Identifier of the announcement in the settings file"))
                .flag(commandManager.flagBuilder("self")
                        .withDescription(Description.of("Shows this message to the sender only")))
                .handler(ctx -> {
                    final Integer id = ctx.get("id");
                    final boolean self = ctx.flags().isPresent("self");
                    final ChatChannel.ChannelType channelType = Social.get().getConfig().getAnnouncements()
                            .isUseActionBar() ? ChatChannel.ChannelType.ACTION_BAR : ChatChannel.ChannelType.CHAT;

                    final Announcement announcement = Social.registries().announcements().values().get(id);
                    if (announcement == null) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getUnknownAnnouncement(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    final var contextBuilder = SocialParserContext.builder(ctx.sender(), announcement.message())
                            .messageChannelType(channelType);

                    if (self) {
                        ctx.sender().sendParsableMessage(contextBuilder.build());
                        return;
                    }

                    Social.get().getUserService().get()
                            .forEach(recipient -> recipient.sendParsableMessage(contextBuilder.build()));
                }));

        // /social channel
        commandManager.command(socialCommand
                .literal("channel")
                .commandDescription(Description.of("Switches your main channel"))
                .permission("social.use.channel")
                .required("channel", ChannelParser.channelParser(), Description.of("The channel to switch to"))
                .senderType(InGameSocialUser.class)
                .handler(ctx -> {
                    final ChatChannel channel = ctx.get("channel");
                    if (channel instanceof GroupChatChannel && !channel.members().contains(ctx.sender())) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    if (channel == ctx.sender().mainChannel().get())
                        return;

                    if (channel.permission().isPresent() && !ctx.sender().checkPermission(channel.permission().get())) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    Social.get().getUserManager().announceChannelSwitch(ctx.sender(), channel);
                }));

        // /social dictionary
        commandManager.command(socialCommand
                .literal("dictionary")
                .commandDescription(Description.of("Opens the dictionary"))
                .permission("social.use.dictionary")
                .required("type", EnumParser.enumParser(DictionaryType.class))
                .senderType(InGameSocialUser.class)
                .handler(ctx -> {
                    final DictionaryType type = ctx.get("type");
                    switch (type) {
                        case EMOJIS -> {
                            SocialMenuContext context = SocialMenuContext.builder()
                                    .viewer(ctx.sender())
                                    .build();

                            EmojiDictionaryMenu dictionary = new EmojiDictionaryMenu();
                            dictionary.open(context);
                        }
                        case KEYWORDS -> {
                            SocialMenuContext context = SocialMenuContext.builder()
                                    .viewer(ctx.sender())
                                    .build();

                            KeywordDictionaryMenu dictionary = new KeywordDictionaryMenu();
                            dictionary.open(context);
                        }
                    }
                }));

        /*
         * // /social history
         * commandManager.command(socialCommand
         * .literal("history")
         * .commandDescription(Description.of("Opens the chat history"))
         * .permission("social.use.history")
         * .handler(ctx -> {
         * final SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
         * .messages(Social.get().getChatManager().getHistory().get())
         * .viewer(ctx.sender())
         * .build();
         * 
         * final HistoryMenu history = new HistoryMenu();
         * history.open(context);
         * })
         * );
         * 
         * // /social history channel <channel>
         * commandManager.command(socialCommand
         * .literal("history")
         * .literal("channel")
         * .required("channel", ChannelParser.channelParser())
         * .handler(ctx -> {
         * final ChatChannel channel = ctx.get("channel");
         * final SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
         * .headerType(HeaderType.CHANNEL)
         * .messages(Social.get().getChatManager().getHistory().getByChannel(channel))
         * .viewer(ctx.sender())
         * .channel(channel)
         * .build();
         * 
         * final HistoryMenu history = new HistoryMenu();
         * history.open(context);
         * })
         * );
         * 
         * // /social history thread <id>
         * commandManager.command(socialCommand
         * .literal("history")
         * .literal("thread")
         * .required("id", RegisteredMessageParser.registeredMessageParser())
         * //.required("id", IntegerParser.integerParser(0,
         * Social.get().getChatManager().getHistory().get().size() - 1))
         * .handler(ctx -> {
         * final SocialRegisteredMessageContext message = ctx.get("id");
         * //final Integer threadId = ctx.get("id");
         * //final SocialRegisteredMessageContext message =
         * Social.get().getChatManager().getHistory().getById(threadId);
         * final SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
         * .headerType(HeaderType.THREAD)
         * .messages(Social.get().getChatManager().getHistory().getThread(message, 128))
         * .viewer(ctx.sender())
         * .build();
         * 
         * final HistoryMenu history = new HistoryMenu();
         * history.open(context);
         * })
         * );
         * 
         * // /social history user <username>
         * commandManager.command(socialCommand
         * .literal("history")
         * .literal("user")
         * .required("user", UserParser.userParser())
         * .handler(ctx -> {
         * final AbstractSocialUser target = ctx.get("user");
         * final SocialHistoryMenuContext context = SocialHistoryMenuContext.builder()
         * .headerType(HeaderType.PLAYER)
         * .messages(Social.get().getChatManager().getHistory().getByUser(target))
         * .viewer(ctx.sender())
         * .target(target)
         * .build();
         * 
         * final HistoryMenu history = new HistoryMenu();
         * history.open(context);
         * })
         * );
         * 
         * // /social info
         * commandManager.command(socialCommand
         * .literal("info")
         * .commandDescription(Description.of("Opens the user information menu"))
         * .permission("social.use.info")
         * .optional("user", UserParser.userParser())
         * .handler(ctx -> {
         * final AbstractSocialUser target = ctx.getOrDefault("user", ctx.sender());
         * final SocialMenuContext context = SocialMenuContext.builder()
         * .viewer(ctx.sender())
         * .target(target)
         * .build();
         * 
         * final PlayerInfoMenu playerInfo = new PlayerInfoMenu();
         * playerInfo.open(context);
         * })
         * );
         * 
         */

        // /social mute
        commandManager.command(socialCommand
                .literal("mute")
                .commandDescription(Description.of("Mutes a user"))
                .permission("social.use.mute")
                .required("user", UserParser.userParser(), Description.of("Username of the player to mute"))
                .optional("channel", ChannelParser.channelParser(), Description.of("Channel where <user> will be muted"))
                .handler(ctx -> {
                    final SocialUser target = ctx.get("user");

                    if (target.equals(ctx.sender())) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                Social.get().getConfig().getMessages().getErrors().getCannotMuteYourself(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    if (target.isOnline() && target.checkPermission("social.mute.excempt")) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                Social.get().getConfig().getMessages().getErrors().getUserExcemptFromMute(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    final ChatChannel channel = ctx.getOrDefault("channel", null);
                    if (channel != null) { // mute
                        if (Social.get().getUserManager().isMuted(target, channel)) {
                            Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                    Social.get().getConfig().getMessages().getErrors().getUserIsAlreadyMuted(),
                                    Social.get().getConfig().getMessages().getChannelType());
                            return;
                        }

                        Social.get().getUserManager().mute(target, channel);

                        // Command sender
                        String successMessage = String.format(
                                Social.get().getConfig().getMessages().getCommands().getUserMuted(),
                                target.displayNameOrUsername().content());
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), channel, successMessage,
                                Social.get().getConfig().getMessages().getChannelType());

                        // Target
                        Social.get().getTextProcessor().parseAndSend(target, channel,
                                Social.get().getConfig().getMessages().getInfo().getUserMuted(),
                                Social.get().getConfig().getMessages().getChannelType());
                    } else { // global mute
                        if (Social.get().getUserManager().isGloballyMuted(target)) {
                            Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                    Social.get().getConfig().getMessages().getErrors().getUserIsAlreadyMuted(),
                                    Social.get().getConfig().getMessages().getChannelType());
                            return;
                        }

                        Social.registries().channels().values().forEach(
                                registeredChannel -> Social.get().getUserManager().mute(target, registeredChannel));

                        // Command sender
                        String successMessage = String.format(
                                Social.get().getConfig().getMessages().getCommands().getUserMutedGlobally(),
                                target.displayNameOrUsername().content());
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                successMessage, Social.get().getConfig().getMessages().getChannelType());

                        // Target
                        Social.get().getTextProcessor().parseAndSend(target, target.mainChannel().get(),
                                Social.get().getConfig().getMessages().getInfo().getUserMutedGlobally());
                    }
                }));

        // /social unmute
        commandManager.command(socialCommand
                .literal("unmute")
                .commandDescription(Description.of("Unmutes a user"))
                .permission("social.use.unmute")
                .required("user", UserParser.userParser(), Description.of("Username of the player to unmute"))
                .optional("channel", ChannelParser.channelParser(), Description.of("Channel where <user> will be unmuted"))
                .handler(ctx -> {
                    final SocialUser target = ctx.get("user");
                    final ChatChannel channel = ctx.getOrDefault("channel", null);

                    if (channel != null) { // unmute in channel
                        if (!Social.get().getUserManager().isMuted(target, channel)) {
                            Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                    Social.get().getConfig().getMessages().getErrors().getUserIsNotMuted(),
                                    Social.get().getConfig().getMessages().getChannelType());
                            return;
                        }

                        Social.get().getUserManager().unmute(target, channel);

                        // Command sender
                        String successMessage = String.format(
                                Social.get().getConfig().getMessages().getCommands().getUserUnmuted(),
                                target.displayNameOrUsername().content());
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), channel, successMessage,
                                Social.get().getConfig().getMessages().getChannelType());

                        // Target
                        Social.get().getTextProcessor().parseAndSend(target, channel,
                                Social.get().getConfig().getMessages().getInfo().getUserUnmuted(),
                                Social.get().getConfig().getMessages().getChannelType());
                    } else { // unmute globally
                        if (!Social.get().getUserManager().isGloballyMuted(target)) {
                            Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                    Social.get().getConfig().getMessages().getErrors().getUserIsNotMuted(),
                                    Social.get().getConfig().getMessages().getChannelType());
                            return;
                        }

                        Social.registries().channels().forEach(c -> Social.get().getUserManager().unmute(target, c));

                        // Command sender
                        String successMessage = String.format(
                                Social.get().getConfig().getMessages().getCommands().getUserUnmutedGlobally(),
                                target.displayName());
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                successMessage, Social.get().getConfig().getMessages().getChannelType());

                        // Target
                        Social.get().getTextProcessor().parseAndSend(target, target.mainChannel().get(),
                                Social.get().getConfig().getMessages().getInfo().getUserUnmutedGlobally(),
                                Social.get().getConfig().getMessages().getChannelType());
                    }
                }));

        // /social nickname set
        commandManager.command(socialCommand
                .literal("nickname")
                .literal("set")
                .commandDescription(Description.of("Sets a nickname"))
                .permission("social.use.nickname.set")
                .required("nickname", StringParser.quotedStringParser(), Description.of("The new nickname"))
                .optional("user", UserParser.userParser(), Description.of("Username of the player to rename"))
                .handler(ctx -> {
                    String nickname = ctx.get("nickname");
                    final SocialUser target = ctx.getOrDefault("user", null);

                    if (nickname.length() > 16) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getNicknameTooLong(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    if (Social.get().getUserService().identityResolver().resolveUuid(nickname).isPresent()) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getNicknameAlreadyInUse(),
                                Social.get().getConfig().getMessages().getChannelType());

                        return;
                    }

                    if (target != null) { // Other user's nickname
                        if (nickname.equalsIgnoreCase("reset"))
                            nickname = target.username();

                        if (!ctx.sender().checkPermission("social.use.nickname.set.others")) {
                            Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                    Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(),
                                    Social.get().getConfig().getMessages().getChannelType());
                            return;
                        }

                        target.displayName().set(Component.text(nickname));
                        Social.get().getTextProcessor()
                                .parseAndSend(ctx.sender(),
                                        String.format(Social.get().getConfig().getMessages().getCommands()
                                                .getNicknameChangedOthers(), target.username(), nickname),
                                        Social.get().getConfig().getMessages().getChannelType());
                    } else { // Sender's nickname
                        if (nickname.equalsIgnoreCase("reset"))
                            nickname = ctx.sender().username();

                        ctx.sender().displayName().set(Component.text(nickname));
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                                Social.get().getConfig().getMessages().getCommands().getNicknameChanged(),
                                Social.get().getConfig().getMessages().getChannelType());
                    }
                }));

        // /social nickname color
        /*
         * commandManager.command(socialCommand
         * .literal("nickname")
         * .literal("color")
         * .commandDescription(Description.of("Sets a nickname color"))
         * .permission("social.use.nickname.color")
         * .required("color", TextColorParser.textColorParser())
         * .optional("user", UserParser.userParser())
         * .handler(ctx -> {
         * final TextColor textColor = ctx.get("color");
         * final SocialUser target = ctx.getOrDefault("user", null);
         * 
         * if (target != null) { // Other user's nickname color
         * if (!ctx.sender().checkPermission("social.use.nickname.color.others")) {
         * Social.get().getTextProcessor().parseAndSend(ctx.sender(),
         * Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(),
         * Social.get().getConfig().getMessages().getChannelType());
         * return;
         * }
         * 
         * target.displayNameStyle().set(Style.style(textColor));
         * Social.get().getTextProcessor().parseAndSend(ctx.sender(),
         * String.format(
         * Social.get().getConfig().getMessages().getCommands().getNicknameChangedOthers
         * (),
         * target.username(), target.cachedDisplayName()),
         * Social.get().getConfig().getMessages().getChannelType());
         * } else { // Sender's nickname color
         * ctx.sender().displayNameStyle().set(Style.style(textColor));
         * Social.get().getTextProcessor().parseAndSend(ctx.sender(),
         * ctx.sender().mainChannel().get(),
         * Social.get().getConfig().getMessages().getCommands().getNicknameChanged(),
         * Social.get().getConfig().getMessages().getChannelType());
         * }
         * }));
         */

        commandManager.command(socialCommand
                .literal("processor")
                .literal("info")
                .commandDescription(Description.of("Shows information about social's text processor"))
                .permission("social.use.processor.info")
                .handler(ctx -> {
                    final String registeredParsers = String.format(
                            Social.get().getConfig().getMessages().getCommands().getProcessorInfoParsers(),
                            Social.get().getTextProcessor().getContextualParsers().size(),
                            Social.get().getTextProcessor().getContextualParsersByType(SocialParserGroup.class).size());

                    ctx.sender().sendParsableMessage(registeredParsers);
                    ctx.sender().sendParsableMessage(getFormattedParserInfo(SocialContextualPlaceholder.class));
                    ctx.sender().sendParsableMessage(getFormattedParserInfo(SocialContextualKeyword.class));
                }));

        // /social processor parse
        commandManager.command(socialCommand
                .literal("processor")
                .literal("parse")
                .commandDescription(Description.of("Parses a message and returns the result"))
                .permission("social.use.processor.parse")
                .required("text", StringParser.greedyFlagYieldingStringParser(), Description.of("The text to process"))
                .flag(commandManager.flagBuilder("user")
                        .withComponent(UserParser.userParser())
                        .withDescription(Description.of("User for the parser context")))
                .flag(commandManager.flagBuilder("channel")
                        .withComponent(ChannelParser.channelParser())
                        .withDescription(Description.of("Channel for the parser context")))
                .flag(commandManager.flagBuilder("type")
                        .withComponent(EnumParser.enumParser(ChatChannel.ChannelType.class))
                        .withDescription(Description.of("Channel type for the parser context")))
                .flag(commandManager.flagBuilder("userInput")
                        .withDescription(Description.of("Whether to parse this message as user input")))
                .handler(ctx -> {
                    final String input = ctx.get("text");
                    final Component message = Component.text(input);

                    final SocialUser user = ctx.flags().getValue("user", ctx.sender());
                    final ChatChannel channel = ctx.flags().getValue("channel", user.mainChannel().get());
                    final ChatChannel.ChannelType type = ctx.flags().getValue("type", ChatChannel.ChannelType.CHAT);
                    final boolean userInput = ctx.flags().getValue("userInput", false);

                    final var context = SocialParserContext.builder(user, message)
                            .channel(channel)
                            .messageChannelType(type)
                            .build();

                    final var processedMessage = Social.get().getTextProcessor().parse(context);
                    var result = processedMessage;
                    if (context.messageChannelType().equals(ChatChannel.ChannelType.CHAT)) {
                        result = Component
                                .text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                                .append(result)
                                .hoverEvent(Component.text(Social.get().getConfig().getMessages().getCommands()
                                        .getProcessorClickToAnnounce()))
                                .clickEvent(ClickEvent.callback(audience -> {
                                    Social.get().getUserService().get()
                                            .forEach(recipient -> recipient.sendMessage(processedMessage));
                                }));
                    }

                    user.sendParsableMessage(context.withMessage(result), userInput);

                }));

        // /social processor get placeholder
        commandManager.command(socialCommand
                .literal("processor")
                .literal("get")
                .literal("placeholder")
                .commandDescription(Description.of("Gets the value of a registered placeholder"))
                .permission("social.use.processor.get.placeholder")
                .required("placeholder", IdentifiedParserParser.of(SocialContextualPlaceholder.class), Description.of("The contextual placeholder that will be resolved and parsed"))
                .flag(commandManager.flagBuilder("user")
                        .withComponent(UserParser.userParser())
                        .withDescription(Description.of("User for the parser context")))
                .handler(ctx -> {
                    final SocialUser user = ctx.flags().getValue("user", ctx.sender());
                    final SocialContextualPlaceholder placeholder = ctx.get("placeholder");

                    final var contextBuilder = SocialParserContext.builder(user, Component.empty());
                    final var optionalGroup = Social.get().getTextProcessor()
                            .getGroupByContextualParser(placeholder.getClass());

                    optionalGroup.ifPresent(contextBuilder::group);

                    user.sendParsableMessage(
                            Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                                    .append(placeholder.get(contextBuilder.build())));
                }));

        // /social processor get keyword
        commandManager.command(socialCommand
                .literal("processor")
                .literal("get")
                .literal("keyword")
                .commandDescription(Description.of("Gets the value of a registered keyword"))
                .permission("social.use.processor.get.keyword")
                .required("keyword", IdentifiedParserParser.of(SocialContextualKeyword.class), Description.of("The contextual keyword that will be resolved and parsed"))
                .flag(commandManager.flagBuilder("user")
                        .withComponent(UserParser.userParser())
                        .withDescription(Description.of("User for the parser context")))
                .handler(ctx -> {
                    final SocialUser user = ctx.flags().getValue("user", ctx.sender());
                    final SocialContextualKeyword keyword = ctx.get("keyword");

                    final var contextBuilder = SocialParserContext.builder(user, Component.empty());
                    final var optionalGroup = Social.get().getTextProcessor()
                            .getGroupByContextualParser(keyword.getClass());

                    optionalGroup.ifPresent(contextBuilder::group);

                    user.sendParsableMessage(
                            Component.text(Social.get().getConfig().getMessages().getCommands().getProcessorResult())
                                    .append(keyword.process(contextBuilder.build())));
                }));

        // /social reload
        commandManager.command(socialCommand
                .literal("reload")
                .commandDescription(Description.of("Reloads the plugin"))
                .permission("social.use.reload")
                .optional("type", EnumParser.enumParser(ReloadType.class))
                .handler(ctx -> {
                    final ReloadType type = ctx.getOrDefault("type", ReloadType.ALL);

                    Social.get().reload(type);
                    Social.get().getTextProcessor().parseAndSend(
                            ctx.sender(),
                            String.format(Social.get().getConfig().getMessages().getCommands().getModuleReloaded(),
                                    type),
                            Social.get().getConfig().getMessages().getChannelType());
                }));

        // /social spy
        commandManager.command(socialCommand
                .literal("spy")
                .commandDescription(Description.of("Toggles the socialspy status"))
                .permission("social.use.spy")
                .senderType(InGameSocialUser.class)
                .handler(ctx -> {
                    ctx.sender().socialSpy().set(!ctx.sender().socialSpy().get());
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), ctx.sender().mainChannel().get(),
                            Social.get().getConfig().getMessages().getCommands().getSocialSpyStatusChanged(),
                            Social.get().getConfig().getMessages().getChannelType());
                }));
    }

    enum DictionaryType {
        EMOJIS,
        KEYWORDS
    }

    private static Component getFormattedParserInfo(Class<? extends SocialContextualParser> type) {
        final var typeParsers = Social.get().getTextProcessor().getContextualParsersByType(type);
        final String message = String.format(
                Social.get().getConfig().getMessages().getCommands().getProcessorInfoParsersByType(),
                type.getSimpleName(),
                typeParsers.size());

        return Component.text(message)
                .hoverEvent(
                        Component.text(typeParsers.stream().map(p -> p.getClass().getSimpleName()).toList().toString(),
                                NamedTextColor.GRAY));
    }

}
