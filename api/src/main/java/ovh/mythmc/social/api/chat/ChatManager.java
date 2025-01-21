package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupAliasChangeEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupCreateEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupDisbandEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaderChangeEvent;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final ChatHistory history = new ChatHistory();

    private final List<ChatChannel> channels = new ArrayList<>();

    public ChatChannel getChannel(final @NotNull String channelName) {
        for (ChatChannel chatChannel : channels) {
            if (chatChannel.getName().equals(channelName))
                return chatChannel;
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByCode(final int code) {
        ChatChannel chatChannel = getChannel("G-" + code);
        if (chatChannel instanceof GroupChatChannel)
            return (GroupChatChannel) chatChannel;

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull UUID uuid) {
        for (ChatChannel channel : getChannels()) {
            if (channel instanceof GroupChatChannel && channel.getMembers().contains(uuid)) {
                return (GroupChatChannel) channel;
            }
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByUser(final @NotNull SocialUser user) {
        return getGroupChannelByUser(user.getUuid());
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        SocialUser previousLeader = Social.get().getUserManager().get(groupChatChannel.getLeaderUuid());
        SocialUser leader = Social.get().getUserManager().get(leaderUuid);

        SocialGroupLeaderChangeEvent socialGroupLeaderChangeEvent = new SocialGroupLeaderChangeEvent(groupChatChannel, previousLeader, leader);
        Bukkit.getPluginManager().callEvent(socialGroupLeaderChangeEvent);

        groupChatChannel.setLeaderUuid(leaderUuid);
    }

    public void setGroupChannelAlias(final @NotNull GroupChatChannel groupChatChannel, final @Nullable String alias) {
        SocialGroupAliasChangeEvent socialGroupAliasChangeEvent = new SocialGroupAliasChangeEvent(groupChatChannel, alias);
        Bukkit.getPluginManager().callEvent(socialGroupAliasChangeEvent);
        
        groupChatChannel.setAlias(socialGroupAliasChangeEvent.getAlias());
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel chatChannel) {
        if (Social.get().getConfig().getSettings().isDebug())
            Social.get().getLogger().info("Registered channel '" + chatChannel.getName() + "'");

        return channels.add(chatChannel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid, final @Nullable String alias) {
        int code = (int) Math.floor(100000 + Math.random() * 900000);
        GroupChatChannel chatChannel = new GroupChatChannel(leaderUuid, alias, code);
        chatChannel.addMember(leaderUuid);

        SocialGroupCreateEvent socialGroupCreateEvent = new SocialGroupCreateEvent(chatChannel);
        Bukkit.getPluginManager().callEvent(socialGroupCreateEvent);
        return registerChatChannel(chatChannel);
    }

    public boolean registerGroupChatChannel(final @NotNull UUID leaderUuid) {
        return registerGroupChatChannel(leaderUuid, null);
    }

    public boolean unregisterChatChannel(final @NotNull ChatChannel chatChannel) {
        if (chatChannel instanceof GroupChatChannel) {
            SocialGroupDisbandEvent socialGroupDisbandEvent = new SocialGroupDisbandEvent((GroupChatChannel) chatChannel);
            Bukkit.getPluginManager().callEvent(socialGroupDisbandEvent);
        }

        if (Social.get().getConfig().getSettings().isDebug())
            Social.get().getLogger().info("Unregistered channel '" + chatChannel.getName() + "'");

        return channels.remove(chatChannel);
    }

    public List<ChatChannel> getVisibleChannels(final @NotNull SocialUser user) {
        return channels.stream()
            .filter(channel -> hasPermission(user, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull SocialUser user) {
        for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
            if (channel.isJoinByDefault()) {
                if (channel.getPermission() == null || user.getPlayer().hasPermission(channel.getPermission()))
                    channel.addMember(user.getUuid());
            }
        }
    }

    public boolean hasGroup(final @NotNull UUID uuid) {
        return getGroupChannelByUser(uuid) != null;
    }

    public boolean hasGroup(final @NotNull SocialUser user) {
        return hasGroup(user.getUuid());
    }

    public boolean hasPermission(final @NotNull SocialUser user, final @NotNull ChatChannel chatChannel) {
        if (chatChannel.getPermission() == null)
            return true;

        if (user.getPlayer().hasPermission(chatChannel.getPermission()))
            return true;

        return false;
    }

    public SocialMessageContext sendChatMessage(final @NotNull SocialUser sender, @NotNull ChatChannel chatChannel, @NotNull String message, Integer replyId) {
        // Prepare and send MessagePrepare event
        SocialChatMessagePrepareEvent socialChatMessagePrepareEvent = new SocialChatMessagePrepareEvent(sender, chatChannel, message, replyId);
        Bukkit.getPluginManager().callEvent(socialChatMessagePrepareEvent);
        if (socialChatMessagePrepareEvent.isCancelled())
            return null;

        // Update values in case they've been changed
        message = socialChatMessagePrepareEvent.getRawMessage();
        chatChannel = socialChatMessagePrepareEvent.getChannel();

        // List of players who will receive this message (channel + socialspy)
        List<UUID> players = new ArrayList<>();

        // Apply socialspy
        for (SocialUser user : Social.get().getUserManager().get()) {
            if (chatChannel.getMembers().contains(user.getUuid())) continue;
            if (user.isSocialSpy())
                players.add(user.getUuid());
        }

        // Channel icon hover text
        Component channelHoverText = text("");
        if (chatChannel.isShowHoverText()) {
            channelHoverText = parse(sender, chatChannel, Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                    .appendNewline()
                    .append(parse(sender, chatChannel, chatChannel.getHoverText()));
        }

        // Text divider
        Component textDivider = parse(sender, chatChannel, " " + chatChannel.getTextDivider() + " ");

        // Sender's nickname
        Component nickname = parse(sender, chatChannel, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat())
                .colorIfAbsent(NamedTextColor.GRAY);

        // Filtered message
        Component filteredMessage = parsePlayerInput(sender, chatChannel, message);

        // This message's context
        SocialMessageContext context = null;
        
        // This message's ID
        int messageId = 0;

        // Apply reply text
        if (socialChatMessagePrepareEvent.isReply() && Social.get().getChatManager().hasPermission(sender, socialChatMessagePrepareEvent.getChannel())) {
            SocialMessageContext reply = Social.get().getChatManager().getHistory().getById(replyId);
            if (reply.isReply())
                replyId = reply.replyId();

            if (!reply.chatChannel().equals(chatChannel) && Social.get().getChatManager().hasPermission(sender, reply.chatChannel()))
                chatChannel = reply.chatChannel();

            context = new SocialMessageContext(sender, chatChannel, message, filteredMessage, replyId);

            messageId = Social.get().getChatManager().getHistory().register(context);

            if (reply.sender().getPlayer() != null) {
                Component nicknameHoverText = Component.empty();

                for (SocialMessageContext threadReply : Social.get().getChatManager().getHistory().getThread(reply, 8)) {
                    nicknameHoverText = nicknameHoverText
                            .append(text(threadReply.sender().getNickname() + ": ", NamedTextColor.GRAY))
                            .append(text(threadReply.rawMessage()).color(NamedTextColor.WHITE))
                            .appendNewline();
                }

                if (Social.get().getChatManager().getHistory().getThread(reply, 9).size() >= 8) {
                    nicknameHoverText = nicknameHoverText
                            .append(text("...", NamedTextColor.BLUE))
                            .appendNewline();
                }

                String formatString = Social.get().getConfig().getSettings().getChat().getReplyFormat();

                nicknameHoverText = nicknameHoverText
                        .appendNewline()
                        .append(parse(reply.sender(), reply.chatChannel(), Social.get().getConfig().getSettings().getChat().getReplyHoverText()));

                nickname = parse(reply.sender(), reply.chatChannel(), formatString)
                        .hoverEvent(HoverEvent.showText(nicknameHoverText))
                        .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "))
                        .appendSpace()
                        .append(Component.text("(#" + reply.id() + ")", NamedTextColor.DARK_GRAY))
                        .appendSpace()
                        .append(nickname);
            }
        }

        if (context == null) {
            context = new SocialMessageContext(sender, chatChannel, message, filteredMessage, replyId);

            messageId = Social.get().getChatManager().getHistory().register(context);
        }

        int idToReply = messageId;
        if (replyId != null)
            idToReply = Integer.min(messageId, replyId);

        Component messagePrefix = Component.empty()
            .append(parse(sender, chatChannel, chatChannel.getIcon() + " ")
                    .hoverEvent(HoverEvent.showText(channelHoverText))
                    .clickEvent(ClickEvent.runCommand("/social:social channel " + chatChannel.getName()))
            )
            .append(trim(nickname))
            .append(textDivider);

        Component chatMessage = Component.empty()
            .append(filteredMessage
                    .applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + idToReply + ") ")))
            )
            .colorIfAbsent(chatChannel.getTextColor());

        // Add channel members
        players.addAll(chatChannel.getMembers());

        // Call SocialChatMessageReceiveEvent for each channel member
        for (UUID uuid : players) {
            SocialUser recipient = Social.get().getUserManager().get(uuid);
            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(sender, recipient, chatChannel, chatMessage, message, replyId, messageId);
            receiveAsync(recipient, messagePrefix, socialChatMessageReceiveEvent);
        }

        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());
        return context;
    }

    public SocialMessageContext chatMessage(final @NotNull SocialUser sender, @NotNull ChatChannel channel, @NotNull String message, Integer replyId) {
        // SocialChatMessagePrepareEvent
        SocialChatMessagePrepareEvent socialChatMessagePrepareEvent = new SocialChatMessagePrepareEvent(sender, channel, message, replyId);
        Bukkit.getPluginManager().callEvent(socialChatMessagePrepareEvent);
        if (socialChatMessagePrepareEvent.isCancelled())
            return null;

        // Update values
        message = socialChatMessagePrepareEvent.getRawMessage();
        channel = socialChatMessagePrepareEvent.getChannel();

        // Filtered message (already parsed)
        Component filteredMessage = parsePlayerInput(sender, channel, message);

        // This message's context
        SocialMessageContext context = null;

        // This message's ID
        int messageId = 0;

        // Reply icon
        Component replyIcon = Component.empty();
        if (socialChatMessagePrepareEvent.isReply() && hasPermission(sender, socialChatMessagePrepareEvent.getChannel())) {
            SocialMessageContext reply = getHistory().getById(replyId);
            if (reply.isReply()) // Set the original message's ID to the replied message ID to keep the thread
                replyId = reply.replyId();

            if (!reply.chatChannel().equals(channel) && hasPermission(sender, reply.chatChannel()))
                channel = reply.chatChannel(); // Switch to the replied message's channel

            // ???
            context = new SocialMessageContext(sender, channel, message, replyIcon, replyId);
            messageId = getHistory().register(context);

            // Check that the original message's sender is still online
            if (reply.sender().getPlayer() != null) {
                // Reply icon hover text
                Component replyIconHoverText = Component.empty();
                for (SocialMessageContext threadReply : getHistory().getThread(reply, 8)) {
                    replyIconHoverText = replyIconHoverText
                        .append(text(threadReply.sender().getNickname(), NamedTextColor.GRAY))
                        .appendSpace()
                        .append(text(":", NamedTextColor.DARK_GRAY))
                        .appendSpace()
                        .append(threadReply.component().color(NamedTextColor.WHITE))
                        .appendNewline();
                }

                // Append "..." if thread is longer
                if (getHistory().getThread(reply, 9).size() >= 8) {
                    replyIconHoverText = replyIconHoverText
                        .append(text("...", NamedTextColor.BLUE))
                        .appendNewline();
                }

                replyIcon = text(Social.get().getConfig().getSettings().getChat().getReplyFormat())
                    .appendSpace()
                    .append(text("(#" + reply.id() + ")", NamedTextColor.DARK_GRAY))
                    .appendSpace()
                    .hoverEvent(replyIconHoverText)
                    .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "));
            }
        }

        // Channel icon hover text
        Component channelHoverText = Component.empty();
        if (channel.isShowHoverText())
            channelHoverText = text(Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                .appendNewline()
                .append(channel.getHoverText());

        // List of recipients for this message
        List<UUID> recipients = new ArrayList<>(channel.getMembers());

        // SocialSpy
        Social.get().getUserManager().get().forEach(user -> {
            if (channel.getMembers().contains(user.getUuid()))
                continue;

            if (user.isSocialSpy())
                recipients.add(user.getUuid());
        });

        /*
        Component cha = Component.empty()
            .append(text("$(channel_icon)"))
            .appendSpace()
            .append(replyIcon)
            .append(text(Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat()))
            .appendSpace()
            .append(text(channel.getTextDivider()))
            .appendSpace()
            .append(filteredMessage); */

        if (context == null) { // Message has not been registered yet
            context = new SocialMessageContext(sender, channel, message, filteredMessage, replyId);
            messageId = getHistory().register(context);
        }

        Component messagePrefix = Component.empty()
            .append(text(channel.getIcon())
                .hoverEvent(channelHoverText)
                .clickEvent(ClickEvent.runCommand("/social:social channel " + channel.getName())))
            .append(text(Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat()))
            .append(text(channel.getTextDivider()));

        messagePrefix = Social.get().getTextProcessor().parse(sender, channel, messagePrefix);

        Component component = Component.empty()
            .append(filteredMessage
                .applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + messageId + ") "))))
            .colorIfAbsent(channel.getTextColor());

        component = Social.get().getTextProcessor().parse(sender, channel, component);

        recipients.forEach(uuid -> {
            SocialUser recipient = Social.get().getUserManager().get(uuid);
            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(sender, recipient, channel, component, message, replyId, messageId);
            receiveAsync(recipient, messagePrefix, socialChatMessageReceiveEvent);
        });

        // Update latest message for sender
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());
        
        // Return this message's context
        return context;
    }

    private void receiveAsync(@NonNull SocialUser recipient, @NonNull Component messagePrefix, @NonNull SocialChatMessageReceiveEvent event) {
        CompletableFuture.supplyAsync(() -> {
            Bukkit.getPluginManager().callEvent(event);
            
            return event;
        }).thenAccept(receivedMessage -> {
            if (receivedMessage.isCancelled())
                return;

            Social.get().getMessageHandlerRegistry().handle(receivedMessage.getRecipient(), receivedMessage);
        });
    }

    private void receiveAsync(@NotNull SocialUser recipient, @NotNull SocialMessageContext context, @NotNull SocialChatMessageReceiveEvent event) {
        CompletableFuture.supplyAsync(() -> {
            Bukkit.getPluginManager().callEvent(event);
            return event;
        }).thenAccept(receivedMessageEvent -> {
            if (receivedMessageEvent.isCancelled())
                return;

            
        })
    }

    @Deprecated(forRemoval = true)
    public void sendPrivateMessage(final @NotNull SocialUser sender,
                                   final @NotNull SocialUser recipient,
                                   final @NotNull String message) {

        Component prefix = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix() + " ");
        Component prefixHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        Component senderNickname = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component senderHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component recipientNickname = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component arrow = parse(recipient, recipient.getMainChannel(), " " + Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow() + " ");

        Component filteredMessage = parsePlayerInput(sender, sender.getMainChannel(), message);

        if (!sender.getNickname().equals(sender.getPlayer().getName()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.getNickname().equals(recipient.getPlayer().getName()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        Component chatMessage = Component.empty()
                .append(prefix
                        .hoverEvent(HoverEvent.showText(prefixHoverText))
                )
                .append(trim(senderNickname)
                        .hoverEvent(HoverEvent.showText(senderHoverText))
                )
                .append(arrow)
                .append(trim(recipientNickname)
                        .hoverEvent(HoverEvent.showText(recipientHoverText))
                )
                .append(text(": ").color(NamedTextColor.GRAY))
                .append(filteredMessage);

        Collection<SocialUser> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getUserManager().get().forEach(player -> {
            if (player.isSocialSpy() && !members.contains(player))
                members.add(player);
        });

        Social.get().getTextProcessor().send(members, chatMessage);
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(Component.text("[PM] " + sender.getNickname() + " -> " + recipient.getNickname() + ": " + message));
    }

    public void newPrivateMessage(SocialUser sender, SocialUser recipient, String message) {
        Component senderNickname = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientNickname = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());

        Component filteredMessage = parsePlayerInput(sender, sender.getMainChannel(), message);

        Component component = Component.empty()
            .append(text(Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix())
                .hoverEvent(text(Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText())))
            .appendSpace()
            .append(senderNickname)
            .appendSpace()
            .append(text(Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow()))
            .appendSpace()
            .append(recipientNickname)
            .append(text(":", NamedTextColor.GRAY))
            .appendSpace()
            .append(filteredMessage);

        Collection<SocialUser> members = new ArrayList<>();
        members.addAll(List.of(sender, recipient));
        
        Social.get().getUserManager().get().forEach(user -> {
            if (user.isSocialSpy() && !members.contains(user))
                members.add(recipient);
        });

        SocialParserContext context = SocialParserContext.builder()
            // User is not necessary since the handler will provide it itself
            .message(filteredMessage)
            .build();

        Social.get().getMessageHandlerRegistry().handle(members, context);
        Social.get().getUserManager().setLatestMessage(sender, System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(Component.text("[PM] " + sender.getNickname() + " -> " + recipient.getNickname() + ": " + message));
    }

    private Component trim(final @NotNull Component component) {
        if (component instanceof TextComponent textComponent) {
            textComponent.content(textComponent.content().trim());
            return textComponent;
        }

        return component;
    }

    private Component parse(SocialUser user, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(message)
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

    private Component parse(SocialUser user, ChatChannel channel, String message) {
        return parse(user, channel, text(message));
    }

    private Component parsePlayerInput(SocialUser user, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder()
            .user(user)
            .channel(channel)
            .message(text(message))
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }

}
