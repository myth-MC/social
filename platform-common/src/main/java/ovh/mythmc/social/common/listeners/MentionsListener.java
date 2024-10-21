package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;
import ovh.mythmc.social.api.events.chat.SocialChatMessageSendEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class MentionsListener implements Listener {

    @EventHandler
    public void onMention(SocialChatMessageReceiveEvent event) {
        if (event.getRecipient().equals(event.getSender()))
            return;

        if (!event.getSender().getPlayer().hasPermission("social.mentions"))
            return;

        String serializedMessage = MiniMessage.miniMessage().serialize(event.getMessage());
        String textDivider = MiniMessage.miniMessage().serialize(Social.get().getTextProcessor().parse(event.getSender(), event.getChatChannel().getTextDivider()));

        serializedMessage = serializedMessage.substring(serializedMessage.indexOf(textDivider));

        Pattern pattern = Pattern.compile("(\\b" + event.getRecipient().getNickname() + "\\b)|(\\b" + event.getRecipient().getPlayer().getName() + "\\b)");

        // Replace player's name and nickname
        if (pattern.matcher(serializedMessage).find()) {
            Component hoverText = Social.get().getTextProcessor().parse(event.getSender(), Social.get().getConfig().getSettings().getChat().getMentionHoverText());

            List<Component> children = new ArrayList<>(List.copyOf(event.getMessage().children()));

            Component replaced = children.get(children.size() - 1).replaceText(TextReplacementConfig.builder()
                    .match(Pattern.compile("\\b" + event.getRecipient().getNickname() + "\\b"))
                    .replacement(Component.text(event.getRecipient().getNickname(), event.getChatChannel().getColor()).hoverEvent(HoverEvent.showText(hoverText)))
                    .build()
            );

            replaced = replaced.replaceText(TextReplacementConfig.builder()
                    .match(Pattern.compile("\\b" + event.getRecipient().getPlayer().getDisplayName() + "\\b"))
                    .replacement(Component.text(event.getRecipient().getPlayer().getDisplayName(), event.getChatChannel().getColor()).hoverEvent(HoverEvent.showText(hoverText)))
                    .build()
            );

            children.set(children.size() - 1, replaced);
            event.setMessage(event.getMessage().children(children));

            event.getRecipient().getPlayer().playSound(event.getRecipient().getPlayer(), Sound.valueOf(Social.get().getConfig().getSettings().getChat().getMentionSound()), 0.75F, 1.75F);
        }
    }

    @EventHandler
    public void onReply(SocialChatMessageReceiveEvent event) {
        if (event.isReply()) {
            Component replyFormat = Social.get().getTextProcessor().parse(event.getRecipient(), Social.get().getConfig().getSettings().getChat().getReplyFormat());
            String replyFormatStripped = MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(replyFormat));

            SocialChatMessageSendEvent socialChatMessageSendEvent = Social.get().getChatManager().getHistory().getById(event.getReplyId());

            if (socialChatMessageSendEvent.getSender().equals(event.getRecipient())) {
                Component replacement = Social.get().getTextProcessor().parse(event.getRecipient(), replyFormat);
                Component message = event.getMessage().replaceText(TextReplacementConfig.builder()
                        .matchLiteral(replyFormatStripped)
                        .replacement(replacement.color(event.getChatChannel().getColor()))
                        .once()
                        .build()
                );

                event.setMessage(message);
            }
        }
    }

}
