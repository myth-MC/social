package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessageReceiveEvent;

import java.util.regex.Pattern;

public final class MentionsListener implements Listener {

    @EventHandler
    public void onMention(SocialChatMessageReceiveEvent event) {
        if (event.getSender().player().isEmpty() || event.getRecipient().player().isEmpty())
            return;

        if (event.getRecipient().equals(event.getSender()))
            return;

        if (!event.getSender().player().get().hasPermission("social.mentions"))
            return;

        var wrapper = new Object() { boolean mentioned = false; };
    
        Component message = event.getMessage();

        // Username
        message = message.replaceText((builder) -> {
            builder
                .match(Pattern.quote("@" + event.getRecipient().player().get().getName()) + "|" + Pattern.quote("@" + event.getRecipient().getNickname()))
                .replacement((match, textBuilder) -> {
                    wrapper.mentioned = true;
                    return Component.text(match.group()).color(event.getChannel().getColor());
                });
        });

        if (wrapper.mentioned) {
            event.getRecipient().playSound(getSoundByKey(Social.get().getConfig().getChat().getMentionSound()));
            
            if (event.getRecipient().isCompanion())
                event.getRecipient().getCompanion().mention(event.getChannel(), event.getSender());
        }

        event.setMessage(message);
    }

    @EventHandler
    public void onReply(SocialChatMessageReceiveEvent event) {
        if (event.isReply()) {
            Component replyFormat = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChannel(), Social.get().getConfig().getSettings().getChat().getReplyFormat());
            String replyFormatStripped = MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(replyFormat));

            SocialMessageContext context = Social.get().getChatManager().getHistory().getById(event.getReplyId());

            if (context.sender().equals(event.getRecipient())) {
                Component replacement = Social.get().getTextProcessor().parse(event.getRecipient(), event.getChannel(), replyFormat);
                Component message = event.getMessage().replaceText(TextReplacementConfig.builder()
                        .matchLiteral(replyFormatStripped)
                        .replacement(replacement.color(event.getChannel().getColor()))
                        .once()
                        .build()
                );

                event.setMessage(message);
            }
        }
    }

    private Sound getSoundByKey(String key) {
        if (!Key.parseable(key))
            return null;

        return Sound.sound(Key.key(key), net.kyori.adventure.sound.Sound.Source.PLAYER, 0.75f, 1.75f);
    }

}
