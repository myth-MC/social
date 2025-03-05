package ovh.mythmc.social.common.callback.handler;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.message.SocialMessageReceiveCallback;
import ovh.mythmc.social.api.context.SocialMessageContext;

import java.util.regex.Pattern;

public final class MentionHandler {

    private static class IdentifierKeys {

        static final String MENTIONS = "social:mentions";
        static final String MENTIONS_REPLY_FORMATTER = "social:mentions-reply-formatter";

    }

    public void registerCallbackHandlers() {
        SocialMessageReceiveCallback.INSTANCE.registerHandler(IdentifierKeys.MENTIONS, (ctx) -> {
            if (ctx.sender().player().isEmpty() || ctx.recipient().player().isEmpty())
                return;

            if (ctx.recipient().equals(ctx.sender()))
                return;

            if (!ctx.sender().checkPermission("social.mentions"))
                return;

            var wrapper = new Object() { boolean mentioned = false; };
        
            Component message = ctx.message();

            // Username
            message = message.replaceText((builder) -> {
                builder
                    .match(Pattern.quote("@" + ctx.recipient().name()) + "|" + Pattern.quote("@" + ctx.recipient().cachedName()))
                    .replacement((match, textBuilder) -> {
                        wrapper.mentioned = true;
                        return Component.text(match.group()).color(ctx.channel().getColor());
                    });
            });

            if (wrapper.mentioned) {
                ctx.recipient().playSound(getSoundByKey(Social.get().getConfig().getChat().getMentionSound()));
                
                ctx.recipient().companion().ifPresent(companion -> companion.mention(ctx.channel(), ctx.sender()));
            }

            ctx.message(message);
        });

        SocialMessageReceiveCallback.INSTANCE.registerHandler(IdentifierKeys.MENTIONS_REPLY_FORMATTER, (ctx) -> {
            if (ctx.isReply()) {
                Component replyFormat = Social.get().getTextProcessor().parse(ctx.recipient(), ctx.channel(), Social.get().getConfig().getChat().getReplyFormat());
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
            IdentifierKeys.MENTIONS,
            IdentifierKeys.MENTIONS_REPLY_FORMATTER
        );
    }

    private static Sound getSoundByKey(String key) {
        if (!Key.parseable(key))
            return null;

        return Sound.sound(Key.key(key), net.kyori.adventure.sound.Sound.Source.PLAYER, 0.75f, 1.75f);
    }

}
