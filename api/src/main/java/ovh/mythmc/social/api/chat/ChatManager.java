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
import ovh.mythmc.social.api.players.SocialPlayer;

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

    public GroupChatChannel getGroupChannelByPlayer(final @NotNull UUID uuid) {
        for (ChatChannel channel : getChannels()) {
            if (channel instanceof GroupChatChannel && channel.getMembers().contains(uuid)) {
                return (GroupChatChannel) channel;
            }
        }

        return null;
    }

    public GroupChatChannel getGroupChannelByPlayer(final @NotNull SocialPlayer socialPlayer) {
        return getGroupChannelByPlayer(socialPlayer.getUuid());
    }

    public void setGroupChannelLeader(final @NotNull GroupChatChannel groupChatChannel,
                                      final @NotNull UUID leaderUuid) {
        SocialPlayer previousLeader = Social.get().getPlayerManager().get(groupChatChannel.getLeaderUuid());
        SocialPlayer leader = Social.get().getPlayerManager().get(leaderUuid);

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

    public List<ChatChannel> getVisibleChannels(final @NotNull SocialPlayer socialPlayer) {
        return channels.stream()
            .filter(channel -> hasPermission(socialPlayer, channel))
            .collect(Collectors.toList());
    }

    public void assignChannelsToPlayer(final @NotNull SocialPlayer socialPlayer) {
        for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
            if (channel.isJoinByDefault()) {
                if (channel.getPermission() == null || socialPlayer.getPlayer().hasPermission(channel.getPermission()))
                    channel.addMember(socialPlayer.getUuid());
            }
        }
    }

    public boolean hasGroup(final @NotNull UUID uuid) {
        return getGroupChannelByPlayer(uuid) != null;
    }

    public boolean hasGroup(final @NotNull SocialPlayer socialPlayer) {
        return hasGroup(socialPlayer.getUuid());
    }

    public boolean hasPermission(final @NotNull SocialPlayer socialPlayer,
                              final @NotNull ChatChannel chatChannel) {

        if (chatChannel.getPermission() == null)
            return true;

        if (socialPlayer.getPlayer().hasPermission(chatChannel.getPermission()))
            return true;

        return false;
    }

    public SocialMessageContext sendChatMessage(final @NotNull SocialPlayer sender, @NotNull ChatChannel chatChannel, @NotNull String message, Integer replyId) {
        // Prepare and send MessagePrepare event
        SocialChatMessagePrepareEvent socialChatMessagePrepareEvent = new SocialChatMessagePrepareEvent(sender, chatChannel, message, replyId);
        Bukkit.getPluginManager().callEvent(socialChatMessagePrepareEvent);
        if (socialChatMessagePrepareEvent.isCancelled())
            return null;

        // Update values in case they've been changed
        message = socialChatMessagePrepareEvent.getRawMessage();
        chatChannel = socialChatMessagePrepareEvent.getChatChannel();

        // List of players who will receive this message (channel + socialspy)
        List<UUID> players = new ArrayList<>();

        // Apply socialspy
        for (SocialPlayer socialPlayer : Social.get().getPlayerManager().get()) {
            if (chatChannel.getMembers().contains(socialPlayer.getUuid())) continue;
            if (socialPlayer.isSocialSpy())
                players.add(socialPlayer.getUuid());
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
                .color(NamedTextColor.GRAY);

        // Filtered message
        Component filteredMessage = parsePlayerInput(sender, chatChannel, message);

        // This message's context
        SocialMessageContext context = null;
        
        // This message's ID
        int messageId = 0;

        // Apply reply text
        if (socialChatMessagePrepareEvent.isReply() && Social.get().getChatManager().hasPermission(sender, socialChatMessagePrepareEvent.getChatChannel())) {
            SocialMessageContext reply = Social.get().getChatManager().getHistory().getById(replyId);
            if (reply.isReply())
                replyId = reply.replyId();

            if (!reply.chatChannel().equals(chatChannel) && Social.get().getChatManager().hasPermission(sender, reply.chatChannel()))
                chatChannel = reply.chatChannel();

            context = SocialMessageContext.builder()
                .sender(sender)
                .chatChannel(chatChannel)
                .rawMessage(message)
                .replyId(replyId)
                .build();

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
            context = SocialMessageContext.builder()
                .sender(sender)
                .chatChannel(chatChannel)
                .rawMessage(message)
                .replyId(replyId)
                .build();

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
            SocialPlayer recipient = Social.get().getPlayerManager().get(uuid);
            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(sender, recipient, chatChannel, chatMessage, message, replyId, messageId);
            receiveAsync(recipient, messagePrefix, socialChatMessageReceiveEvent);
        }

        Social.get().getPlayerManager().setLatestMessage(sender, System.currentTimeMillis());
        return context;
    }

    private void receiveAsync(@NonNull SocialPlayer recipient, @NonNull Component messagePrefix, @NonNull SocialChatMessageReceiveEvent event) {
        CompletableFuture.supplyAsync(() -> {
            Bukkit.getPluginManager().callEvent(event);
            
            return event;
        }).thenAccept(receivedMessage -> {
            if (receivedMessage.isCancelled())
                return;

            Social.get().getTextProcessor().send(receivedMessage.getRecipient(), messagePrefix.append(receivedMessage.getMessage()), receivedMessage.getChatChannel().getType());
        });
    }

    public void sendPrivateMessage(final @NotNull SocialPlayer sender,
                                   final @NotNull SocialPlayer recipient,
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

        Collection<SocialPlayer> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getPlayerManager().get().forEach(player -> {
            if (player.isSocialSpy() && !members.contains(player))
                members.add(player);
        });

        Social.get().getTextProcessor().send(members, chatMessage, ChannelType.CHAT);
        Social.get().getPlayerManager().setLatestMessage(sender, System.currentTimeMillis());

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

    private Component parse(SocialPlayer socialPlayer, ChatChannel channel, Component message) {
        SocialParserContext context = SocialParserContext.builder()
            .socialPlayer(socialPlayer)
            .playerChannel(channel)
            .message(message)
            .messageChannelType(channel.getType())
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

    private Component parse(SocialPlayer socialPlayer, ChatChannel channel, String message) {
        return parse(socialPlayer, channel, text(message));
    }

    private Component parsePlayerInput(SocialPlayer socialPlayer, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder()
            .socialPlayer(socialPlayer)
            .playerChannel(channel)
            .message(text(message))
            .messageChannelType(channel.getType())
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }

}
