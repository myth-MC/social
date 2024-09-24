package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ChatManager {

    public static final ChatManager instance = new ChatManager();
    private final TextColor INFO_COLOR = TextColor.color(106, 178, 197);

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

    public void sendChatMessage(final @NotNull SocialPlayer player,
                                final @NotNull ChatChannel chatChannel,
                                final @NotNull String message) {

        Component channelHoverText = Social.get().getTextProcessor().process(player, Social.get().getConfig().getSettings().getChat().getChannelHoverText())
                .appendNewline()
                .append(Social.get().getTextProcessor().process(player, chatChannel.getHoverText()));

        Component playerHoverText = Social.get().getTextProcessor().process(player, Social.get().getConfig().getSettings().getChat().getPlayerHoverText());
        if (!player.getNickname().equals(player.getPlayer().getName()))
            playerHoverText = playerHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().process(player, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        // Todo: parse PlaceholderAPI
        Component formattedNickname =
                Social.get().getTextProcessor().process(player, Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat())
                .color(chatChannel.getNicknameColor());

        // Todo: event
        Component chatMessage =
                text("")
                        .append(text(chatChannel.getIcon() + " ")
                                .color(chatChannel.getIconColor())
                                .hoverEvent(HoverEvent.showText(channelHoverText))
                                .clickEvent(ClickEvent.runCommand("/social channel " + chatChannel.getName()))
                        )
                        .append(formattedNickname
                                .hoverEvent(HoverEvent.showText(playerHoverText))
                                .clickEvent(ClickEvent.suggestCommand("/pm " + player.getPlayer().getName() + " "))
                        )
                        .append(text(" " + chatChannel.getTextSeparator() + " ")
                                .color(NamedTextColor.GRAY)
                        )
                        .append(text(message)
                                .color(chatChannel.getTextColor())
                        );

        Collection<SocialPlayer> members = new ArrayList<>();
        chatChannel.getMembers().forEach(uuid -> {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
            members.add(socialPlayer);
        });

        Social.get().getTextProcessor().send(members, chatMessage, chatChannel.getType());
        player.setLatestMessageInMilliseconds(System.currentTimeMillis());
    }

    public void sendPrivateMessage(final @NotNull SocialPlayer sender,
                                   final @NotNull SocialPlayer recipient,
                                   final @NotNull String message) {
        // Todo: event
        Component hoverText = Social.get().getTextProcessor().process(sender, Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        Component senderHoverText = Social.get().getTextProcessor().process(sender, Social.get().getConfig().getSettings().getChat().getPlayerHoverText());
        Component recipientHoverText = Social.get().getTextProcessor().process(recipient, Social.get().getConfig().getSettings().getChat().getPlayerHoverText());

        if (!sender.getNickname().equals(sender.getPlayer().getName()))
            senderHoverText = senderHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().process(sender, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        if (!recipient.getNickname().equals(recipient.getPlayer().getName()))
            recipientHoverText = recipientHoverText
                    .appendNewline()
                    .append(Social.get().getTextProcessor().process(recipient, Social.get().getConfig().getSettings().getChat().getPlayerAliasWarningHoverText()));

        Collection<SocialPlayer> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Social.get().getPlayerManager().get().forEach(player -> {
            if (player.isSocialSpy() && !members.contains(player))
                members.add(player);
        });

        Component chatMessage =
                text("")
                        .append(text("✉ ")
                                .color(NamedTextColor.GREEN)
                                .hoverEvent(HoverEvent.showText(hoverText))
                        )
                        .append(text(sender.getNickname())
                                .color(INFO_COLOR)
                                .hoverEvent(HoverEvent.showText(senderHoverText))
                                .clickEvent(ClickEvent.suggestCommand("/pm " + sender.getPlayer().getName() + " "))
                        )
                        .append(text(" ➡ ")
                                .color(NamedTextColor.GRAY)
                        )
                        .append(text(recipient.getNickname())
                                .color(INFO_COLOR)
                                .hoverEvent(HoverEvent.showText(recipientHoverText))
                                .clickEvent(ClickEvent.suggestCommand("/pm " + recipient.getPlayer().getName() + " "))
                        )
                        .append(text(": ")
                                .color(NamedTextColor.GRAY)
                        )
                        .append(text(message)
                                .color(NamedTextColor.WHITE)
                        );

        Social.get().getTextProcessor().send(members, chatMessage, ChannelType.CHAT);
        sender.setLatestMessageInMilliseconds(System.currentTimeMillis());
    }



}
