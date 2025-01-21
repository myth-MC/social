package ovh.mythmc.social.api.context;

import java.util.Collection;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.handlers.MessageHandlerOptions;
import ovh.mythmc.social.api.handlers.RegisteredMessageHandler;
import ovh.mythmc.social.api.users.SocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@RequiredArgsConstructor
@Builder
public final class SocialHandlerContext implements SocialContext {

    @Singular private final Collection<SocialUser> recipients;

    private final Component message;

    private final Collection<RegisteredMessageHandler<? extends MessageHandlerOptions>> handlers;

    private final @Nullable SocialContext source;

    public static SocialHandlerContextBuilder builderFromMessage(@NotNull SocialMessageContext context) {
        return SocialHandlerContext.builder()
            .recipient(context.sender())
            .message(context.component())
            .source(context);
    }

    public static SocialHandlerContextBuilder builderFromParser(@NotNull SocialParserContext context) {
        return SocialHandlerContext.builder()
            .recipient(context.user())
            .message(context.message())
            .source(context);
    }

    public void handle() {
        Social.get().getMessageHandlerRegistry().handle(recipients, this);
    }

    public static class SocialHandlerContextBuilder {

        public SocialHandlerContextBuilder handler(@NotNull RegisteredMessageHandler<? extends MessageHandlerOptions> handler) {
            this.handlers.add(handler);
            return this;
        }
    
        public SocialHandlerContextBuilder handler(@NotNull Key handlerKey) {
            RegisteredMessageHandler<?> handler = Social.get().getMessageHandlerRegistry().getByKey(handlerKey);
            if (handler == null)
                return this;

            return handler(handler);
        }
    
        public SocialHandlerContextBuilder handler(@NotNull String handlerKey) {
            return handler(Key.key(handlerKey));
        }

    }

}
