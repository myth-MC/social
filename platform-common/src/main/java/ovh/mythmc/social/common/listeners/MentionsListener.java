package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class MentionsListener implements Listener {

    @EventHandler
    public void onMention(SocialChatMessageReceiveEvent event) {
        if (event.getRecipient().equals(event.getSender()))
            return;

        if (!event.getSender().getPlayer().hasPermission("social.mentions"))
            return;

        Pattern pattern = Pattern.compile("(^" + Pattern.quote("@" + event.getRecipient().getNickname()) + "$)|(^" + "@" + Pattern.quote(event.getRecipient().getPlayer().getName()) + "$)");

        Arrays.asList(event.getRawMessage().split("\\s+")).forEach(word -> {
            // Replace player's name and nickname
            if (pattern.matcher(word).find()) {
                Component hoverText = Social.get().getTextProcessor().parse(event.getSender(), event.getChatChannel(), Social.get().getConfig().getSettings().getChat().getMentionHoverText());

                List<Component> children = new ArrayList<>(List.copyOf(event.getMessage().children()));

                Component replaced = children.get(children.size() - 1);

                if (word.substring(1).equalsIgnoreCase(event.getRecipient().getNickname())) {
                    // Nickname
                    replaced = replaced.replaceText(TextReplacementConfig.builder()
                        .matchLiteral("@" + event.getRecipient().getNickname())
                        .replacement(Component.text("@" + event.getRecipient().getNickname(), event.getChatChannel().getColor()).hoverEvent(HoverEvent.showText(hoverText)))
                        .build()
                    );
                } else if (word.substring(1).equalsIgnoreCase(event.getRecipient().getPlayer().getName())) {
                    // Username
                    replaced = replaced.replaceText(TextReplacementConfig.builder()
                            .matchLiteral("@" + event.getRecipient().getPlayer().getName())
                            .replacement(Component.text("@" + event.getRecipient().getPlayer().getName(), event.getChatChannel().getColor()).hoverEvent(HoverEvent.showText(hoverText)))
                            .build()
                    );
                }

                children.set(children.size() - 1, replaced);
                event.setMessage(event.getMessage().children(children));

                event.getRecipient().getPlayer().playSound(event.getRecipient().getPlayer(), Sound.valueOf(Social.get().getConfig().getSettings().getChat().getMentionSound()), 0.75F, 1.75F);
            }
        });
    }

    @EventHandler
    public void onReply(SocialChatMessageReceiveEvent event) {
        if (event.isReply()) {
            Component replyFormat = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChatChannel(), Social.get().getConfig().getSettings().getChat().getReplyFormat());
            String replyFormatStripped = MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(replyFormat));

            SocialMessageContext context = Social.get().getChatManager().getHistory().getById(event.getReplyId());

            if (context.sender().equals(event.getRecipient())) {
                Component replacement = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChatChannel(), replyFormat);
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
