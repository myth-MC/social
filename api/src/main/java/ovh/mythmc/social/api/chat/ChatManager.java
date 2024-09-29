package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private final List<ChatChannel> channels = new ArrayList<>();

    public ChatChannel getChannel(final @NotNull String channelName) {
        for (ChatChannel chatChannel : channels) {
            if (chatChannel.getName().equals(channelName))
                return chatChannel;
        }

        return null;
    }

    public boolean exists(final @NotNull String channelName) {
        return getChannel(channelName) != null;
    }

    public boolean registerChatChannel(final @NotNull ChatChannel chatChannel) {
        return channels.add(chatChannel);
    }

    public boolean unregisterChatChannel(final @NotNull ChatChannel chatChannel) {
        return channels.remove(chatChannel);
    }

    public void assignChannelsToPlayer(final @NotNull SocialPlayer socialPlayer) {
        for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
            if (channel.isJoinByDefault()) {
                if (channel.getPermission() == null || socialPlayer.getPlayer().hasPermission(channel.getPermission()))
                    channel.addMember(socialPlayer.getUuid());
            }
        }
    }

    public void sendChatMessage(final @NotNull SocialPlayer sender,
                                final @NotNull ChatChannel chatChannel,
                                final @NotNull String message) {

        Component channelHoverText = text("");
        if (chatChannel.isShowHoverText()) {
            channelHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(sender, chatChannel.getHoverText()));
        }

        Component textDivider = Social.get().getTextProcessor().parse(sender, " " + chatChannel.getTextDivider() + " ");

        Component nickname = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());

        Component filteredMessage = Social.get().getTextProcessor().parsePlayerInput(sender, message);

        Component chatMessage =
                text("")
                        .append(Social.get().getTextProcessor().parse(sender, chatChannel.getIcon() + " ")
                                .hoverEvent(HoverEvent.showText(channelHoverText))
                                .clickEvent(ClickEvent.runCommand("/social:social channel " + chatChannel.getName()))
                        )
                        .append(nickname)
                        .append(textDivider)
                        .append(filteredMessage)
                        .color(chatChannel.getTextColor());

        // Call SocialChatMessageReceiveEvent for each channel member
        Map<SocialPlayer, Component> playerMap = new HashMap<>();
        chatChannel.getMembers().forEach(uuid -> {
            SocialPlayer member = Social.get().getPlayerManager().get(uuid);
            SocialChatMessageReceiveEvent socialChatMessageReceiveEvent = new SocialChatMessageReceiveEvent(sender, member, chatChannel, chatMessage);
            if (!socialChatMessageReceiveEvent.isCancelled())
                playerMap.put(member, socialChatMessageReceiveEvent.getMessage());
        });

        playerMap.forEach((s, m) -> Social.get().getTextProcessor().send(s, m, chatChannel.getType()));
        sender.setLatestMessageInMilliseconds(System.currentTimeMillis());
    }

    public void sendPrivateMessage(final @NotNull SocialPlayer sender,
                                   final @NotNull SocialPlayer recipient,
                                   final @NotNull String message) {

        Component prefix = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix() + " ");
        Component prefixHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        Component senderNickname = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component senderHoverText = Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component recipientNickname = Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        Component arrow = Social.get().getTextProcessor().parse(recipient, " " + Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow() + " ");

        Component filteredMessage = Social.get().getTextProcessor().parsePlayerInput(sender, message);

        if (!sender.getNickname().equals(sender.getPlayer().getName()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(sender, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.getNickname().equals(recipient.getPlayer().getName()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().parse(recipient, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        Component chatMessage = Component.empty()
                .append(prefix
                        .hoverEvent(HoverEvent.showText(prefixHoverText))
                )
                .append(senderNickname
                        .hoverEvent(HoverEvent.showText(senderHoverText))
                )
                .append(arrow)
                .append(recipientNickname
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
        sender.setLatestMessageInMilliseconds(System.currentTimeMillis());

        // Send message to console
        SocialAdventureProvider.get().console().sendMessage(chatMessage);
    }

}
