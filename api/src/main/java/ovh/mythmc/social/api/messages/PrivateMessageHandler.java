package ovh.mythmc.social.api.messages;

import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.channels.ChannelType;
import ovh.mythmc.social.api.channels.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialPrivateMessageContext;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class PrivateMessageHandler {

    public void send(@NotNull SocialPrivateMessageContext context) {
        final SocialPlayer sender = context.sender();
        final SocialPlayer recipient = context.recipient();

        final Component prefix = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix() + " ");
        final Component prefixHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getCommands().getPrivateMessage().hoverText());

        final Component senderNickname = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component senderHoverText = parse(sender, sender.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        final Component recipientNickname = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());
        Component recipientHoverText = parse(recipient, recipient.getMainChannel(), Social.get().getConfig().getSettings().getChat().getClickableNicknameHoverText());

        final Component arrow = parse(recipient, recipient.getMainChannel(), " " + Social.get().getConfig().getSettings().getCommands().getPrivateMessage().arrow() + " ");

        final Component filteredMessage = parsePlayerInput(sender, sender.getMainChannel(), context.rawMessage());

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
                .append(Component.text(": ").color(NamedTextColor.GRAY))
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
        SocialAdventureProvider.get().console().sendMessage(Component.text("[PM] " + sender.getNickname() + " -> " + recipient.getNickname() + ": " + context.rawMessage()));
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
        return parse(socialPlayer, channel, Component.text(message));
    }

    private Component parsePlayerInput(SocialPlayer socialPlayer, ChatChannel channel, String message) {
        SocialParserContext context = SocialParserContext.builder()
            .socialPlayer(socialPlayer)
            .playerChannel(channel)
            .message(Component.text(message))
            .messageChannelType(channel.getType())
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context);
    }
    
}
