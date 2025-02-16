package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.callbacks.key.IdentifierKey;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callbacks.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.context.SocialMessageContext;

import java.util.regex.Pattern;

public final class MentionsListener {

    public void registerCallbackHandlers() {
        SocialMessageReceiveCallback.INSTANCE.registerHandler("social:mentions", (ctx) -> {
            if (ctx.sender().player().isEmpty() || ctx.recipient().player().isEmpty())
                return;

            if (ctx.recipient().equals(ctx.sender()))
                return;

            if (!ctx.sender().player().get().hasPermission("social.mentions"))
                return;

            var wrapper = new Object() { boolean mentioned = false; };
        
            Component message = ctx.message();

            // Username
            message = message.replaceText((builder) -> {
                builder
                    .match(Pattern.quote("@" + ctx.recipient().player().get().getName()) + "|" + Pattern.quote("@" + ctx.recipient().getNickname()))
                    .replacement((match, textBuilder) -> {
                        wrapper.mentioned = true;
                        return Component.text(match.group()).color(ctx.channel().getColor());
                    });
            });

            if (wrapper.mentioned) {
                ctx.recipient().playSound(getSoundByKey(Social.get().getConfig().getChat().getMentionSound()));
                
                if (ctx.recipient().isCompanion())
                    ctx.recipient().getCompanion().mention(ctx.channel(), ctx.sender());
            }

            ctx.message(message);
        });

        SocialMessageReceiveCallback.INSTANCE.registerHandler("social:mentionReplyFormatter", (ctx) -> {
            if (ctx.isReply()) {
                Component replyFormat = Social.get().getTextProcessor().parse(ctx.recipient(), ctx.channel(), Social.get().getConfig().getSettings().getChat().getReplyFormat());
                String replyFormatStripped = MiniMessage.miniMessage().stripTags(MiniMessage.miniMessage().serialize(replyFormat));
    
                SocialMessageContext context = Social.get().getChatManager().getHistory().getById(ctx.replyId());
    
                if (context.sender().equals(ctx.recipient())) {
                    Component replacement = Social.get().getTextProcessor().parse(ctx.recipient(), ctx.channel(), replyFormat);
                    Component message = ctx.message().replaceText(TextReplacementConfig.builder()
                            .matchLiteral(replyFormatStripped)
                            .replacement(replacement.color(ctx.channel().getColor()))
                            .once()
                            .build()
                    );
    
                    ctx.message(message);
                }
            }
        });
    }

    public void unregisterCallbackHandlers() {
        SocialMessageReceiveCallback.INSTANCE.unregisterHandlers(
            IdentifierKey.of("social", "mentions"),
            IdentifierKey.of("social", "mentionReplyFormatter")
        );
    }

    private static Sound getSoundByKey(String key) {
        if (!Key.parseable(key))
            return null;

        return Sound.sound(Key.key(key), net.kyori.adventure.sound.Sound.Source.PLAYER, 0.75f, 1.75f);
    }

}
