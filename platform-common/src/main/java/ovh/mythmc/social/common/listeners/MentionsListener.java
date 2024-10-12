package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;

public final class MentionsListener implements Listener {

    @EventHandler
    public void onSocialChatMessageReceive(SocialChatMessageReceiveEvent event) {
        if (event.getRecipient().equals(event.getSender()))
            return;

        if (!event.getSender().getPlayer().hasPermission("social.mentions"))
            return;

        String serializedMessage = MiniMessage.miniMessage().serialize(event.getMessage());
        String textDivider = MiniMessage.miniMessage().serialize(Social.get().getTextProcessor().parse(event.getSender(), event.getChatChannel().getTextDivider()));

        serializedMessage = serializedMessage.substring(serializedMessage.indexOf(textDivider));

        if (serializedMessage.contains(event.getRecipient().getNickname()) || serializedMessage.contains(event.getRecipient().getPlayer().getName())) {
            Component hoverText = Social.get().getTextProcessor().parse(event.getSender(), Social.get().getConfig().getSettings().getChat().getMentionHoverText());

            event.setMessage(event.getMessage().replaceText(TextReplacementConfig
                    .builder()
                    .matchLiteral(event.getRecipient().getNickname())
                    .replacement(Component.text(event.getRecipient().getNickname(), NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(hoverText)))
                    .build()
            ));

            event.setMessage(event.getMessage().replaceText(TextReplacementConfig
                    .builder()
                    .matchLiteral(event.getRecipient().getPlayer().getName())
                    .replacement(Component.text(event.getRecipient().getPlayer().getName(), NamedTextColor.GREEN).hoverEvent(HoverEvent.showText(hoverText)))
                    .build()
            ));

            event.getRecipient().getPlayer().playSound(event.getRecipient().getPlayer(), Sound.valueOf(Social.get().getConfig().getSettings().getChat().getMentionSound()), 0.75F, 1.75F);
        }
    }

}
