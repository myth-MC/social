package ovh.mythmc.social.api.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.channels.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class ChatMessageHandler {

    public void send(@NotNull SocialMessageContext context) {
        // Prepare and send MessagePrepare event
        SocialChatMessagePrepareEvent socialChatMessagePrepareEvent = new SocialChatMessagePrepareEvent(context.sender(), context.chatChannel(), context.rawMessage(), context.replyId());
        Bukkit.getPluginManager().callEvent(socialChatMessagePrepareEvent);
        if (socialChatMessagePrepareEvent.isCancelled())
            return;

        // Update values in case they've been changed
        SocialPlayer sender = socialChatMessagePrepareEvent.getSender();
        String message = socialChatMessagePrepareEvent.getRawMessage();
        ChatChannel chatChannel = socialChatMessagePrepareEvent.getChatChannel();
        Integer replyId = socialChatMessagePrepareEvent.getReplyId();

        // List of players who will receive this message (channel + socialspy)
        List<UUID> players = new ArrayList<>(chatChannel.getMembers());

        // Apply socialspy
        Social.get().getPlayerManager().get().stream()
            .filter(player -> !players.contains(player.getUuid()) && player.isSocialSpy())
            .map(player -> player.getUuid())
            .forEach(players::add);

        // Channel icon hover text
        Component channelHoverText = Component.empty();
        if (chatChannel.isShowHoverText()) {
            channelHoverText = parse(context.sender(), chatChannel, Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                    .appendNewline()
                    .append(parse(context.sender(), chatChannel, chatChannel.getHoverText()));
        }

        // Text divider
        Component textDivider = parse(context.sender(), chatChannel, " " + chatChannel.getTextDivider() + " ");

        // Sender's nickname
        Component nickname = parse(context.sender(), chatChannel, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat())
                .color(NamedTextColor.GRAY);

        // Filtered message
        Component filteredMessage = parsePlayerInput(context.sender(), chatChannel, message);

        // This message's context
        SocialMessageContext currentMessageContext = null;
        
        // This message's ID
        int messageId = 0;

        // Apply reply text
        if (socialChatMessagePrepareEvent.isReply() && Social.get().getChannelManager().hasPermission(context.sender(), socialChatMessagePrepareEvent.getChatChannel())) {
            SocialMessageContext reply = Social.get().getChatManager().getHistory().getById(replyId);
            if (reply.isReply())
                replyId = reply.replyId();

            if (!reply.chatChannel().equals(chatChannel) && Social.get().getChannelManager().hasPermission(context.sender(), reply.chatChannel()))
                chatChannel = reply.chatChannel();

            currentMessageContext = SocialMessageContext.builder()
                .sender(sender)
                .chatChannel(chatChannel)
                .rawMessage(message)
                .replyId(replyId)
                .build();

            messageId = Social.get().getChatManager().getHistory().register(currentMessageContext);

            if (reply.sender().getPlayer() != null) {
                Component nicknameHoverText = Component.empty();

                for (SocialMessageContext threadReply : Social.get().getChatManager().getHistory().getThread(reply, 8)) {
                    nicknameHoverText = nicknameHoverText
                            .append(Component.text(threadReply.sender().getNickname() + ": ", NamedTextColor.GRAY))
                            .append(Component.text(threadReply.rawMessage()).color(NamedTextColor.WHITE))
                            .appendNewline();
                }

                if (Social.get().getChatManager().getHistory().getThread(reply, 9).size() >= 8) {
                    nicknameHoverText = nicknameHoverText
                            .append(Component.text("...", NamedTextColor.BLUE))
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

        if (currentMessageContext == null) {
            currentMessageContext = SocialMessageContext.builder()
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

        Component chatMessage =
                Component.empty()
                        .append(parse(sender, chatChannel, chatChannel.getIcon() + " ")
                                .hoverEvent(HoverEvent.showText(channelHoverText))
                                .clickEvent(ClickEvent.runCommand("/social:social channel " + chatChannel.getName()))
                        )
                        .append(trim(nickname))
                        .append(textDivider)
                        .append(filteredMessage
                                .applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + idToReply + ") ")))
                        )
                        .color(chatChannel.getTextColor());

        // Call SocialChatMessageReceiveEvent for each channel member
        Map<SocialPlayer, Component> playerMap = new HashMap<>();
        for (UUID uuid : players) {
            SocialPlayer member = Social.get().getPlayerManager().get(uuid);

            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(context.sender(), member, chatChannel, chatMessage, message, replyId, messageId);
            Bukkit.getPluginManager().callEvent(socialChatMessageReceiveEvent);
            if (!socialChatMessageReceiveEvent.isCancelled())
                playerMap.put(member, socialChatMessageReceiveEvent.getMessage());
        }

        playerMap.entrySet().forEach(entry -> Social.get().getTextProcessor().send(entry.getKey(), entry.getValue(), chatChannel.getType()));

        // Update sender's latest message date
        Social.get().getPlayerManager().setLatestMessage(context.sender(), System.currentTimeMillis());
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
            .build();

        return Social.get().getTextProcessor().parse(context);
    }

    private Component parse(SocialPlayer socialPlayer, ChatChannel channel, String message) {
        return parse(socialPlayer, channel, Component.text(message));
    }

    private Component parsePlayerInput(SocialPlayer socialPlayer, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder()
            .socialPlayer(socialPlayer)
            .playerChannel(channel)
            .message(Component.text(message))
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }
    
}
