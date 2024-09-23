package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

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

    public void sendChatMessage(final @NotNull SocialPlayer player,
                                final @NotNull ChatChannel chatChannel,
                                final @NotNull String message) {

        Component channelHoverText = Social.get().getPlaceholderProcessor().process(player, Social.get().getSettings().get().getChat().getChannelHoverText())
                .appendNewline()
                .append(Social.get().getPlaceholderProcessor().process(player, chatChannel.getHoverText()));

        Component playerHoverText = Social.get().getPlaceholderProcessor().process(player, Social.get().getSettings().get().getChat().getPlayerHoverText());

        // Todo: parse PlaceholderAPI
        Component formattedNickname =
                Social.get().getPlaceholderProcessor().process(player, Social.get().getSettings().get().getChat().getPlayerNicknameFormat())
                .color(chatChannel.getNicknameColor());

        // Todo: event
        Component chatMessage =
                text("")
                        .append(text(chatChannel.getIcon() + " ")
                                .color(chatChannel.getIconColor())
                                .hoverEvent(HoverEvent.showText(channelHoverText))
                                .clickEvent(ClickEvent.runCommand("/channel " + chatChannel.getName()))
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

        sendMessage(members, chatMessage);
    }

    public void sendPrivateMessage(final @NotNull SocialPlayer sender,
                                   final @NotNull SocialPlayer recipient,
                                   final @NotNull String message) {
        // Todo: event
        Collection<SocialPlayer> members = new ArrayList<>();
        members.add(sender);
        members.add(recipient);

        Component chatMessage =
                text("")
                        .append(text("✉ ")
                                .color(NamedTextColor.GREEN)
                                .hoverEvent(HoverEvent.showText(translatable("pm.hover.description",
                                        text(sender.getNickname())
                                                .color(INFO_COLOR),
                                        text(recipient.getNickname())
                                                .color(INFO_COLOR)
                                )))
                        )
                        .append(text(sender.getNickname())
                                .color(INFO_COLOR)
                                .hoverEvent(HoverEvent.showText(translatable("pm.hover.sender",
                                        text(sender.getNickname())
                                                .color(INFO_COLOR)
                                )))
                                .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getPlayer().getName() + " "))
                        )
                        .append(text(" ➡ ")
                                .color(NamedTextColor.GRAY)
                        )
                        .append(text(recipient.getNickname())
                                .color(INFO_COLOR)
                                .hoverEvent(HoverEvent.showText(translatable("pm.hover.recipient",
                                        text(recipient.getNickname())
                                                .color(INFO_COLOR)
                                )))
                                .clickEvent(ClickEvent.suggestCommand("/msg " + recipient.getPlayer().getName() + " "))
                        )
                        .append(text(": ")
                                .color(NamedTextColor.GRAY)
                        )
                        .append(text(message)
                                .color(NamedTextColor.WHITE)
                        );

        sendMessage(members, chatMessage);
    }

    private void sendMessage(final @NotNull Collection<SocialPlayer> members,
                             final @NotNull ComponentLike message) {
        members.forEach(chatPlayer -> SocialAdventureProvider.get().sendMessage(chatPlayer, message));
    }

}
