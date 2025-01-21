package ovh.mythmc.social.api.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.users.SocialUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageHandlerRegistry {

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Social.get().getLogger().info("[message-handler] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Social.get().getLogger().warn("[message-handler] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Social.get().getLogger().error("[message-handler] " + message, args);
        }
    };

    public static final MessageHandlerRegistry instance = new MessageHandlerRegistry();

    private final Map<Key, RegisteredMessageHandler<? extends MessageHandlerOptions>> handlerMap = new HashMap<>();

    public <O extends MessageHandlerOptions> RegisteredMessageHandler<O> register(final @NotNull Key key, final @NotNull MessageHandler<O> handler, final @NotNull O options) {
        RegisteredMessageHandler<O> registeredMessageHandler = new RegisteredMessageHandler<O>(handler, options);
        handlerMap.put(key, registeredMessageHandler);
        return registeredMessageHandler;
    }

    public RegisteredMessageHandler<MessageHandlerOptions> register(final @NotNull Key key, final @NotNull MessageHandler<MessageHandlerOptions> handler) {
        return register(key, handler, new MessageHandlerOptions());
    }

    public RegisteredMessageHandler<MessageHandlerOptions> register(final @NotNull Key key, final @NotNull MessageHandler<MessageHandlerOptions> handler, final @NotNull Consumer<MessageHandlerOptions> options) {
        MessageHandlerOptions messageHandlerOptions = new MessageHandlerOptions();
        options.accept(messageHandlerOptions);
        return register(key, handler, messageHandlerOptions);
    }

    public void unregister(final @NotNull Key key) {
        handlerMap.remove(key);
    }

    public @Nullable RegisteredMessageHandler<? extends MessageHandlerOptions> getByKey(final @NotNull Key key) {
        return handlerMap.get(key);
    }

    public void handle(@NotNull SocialUser recipient, @NotNull SocialHandlerContext context) {
        if (context.handlers() == null || context.handlers().isEmpty())
            logger.warn("Message {} has no handlers!", context.message().toString());

        context.handlers().forEach(handler -> {
            MessageHandlerResult result = handler.handle(recipient, context);

            if (Social.get().getConfig().getSettings().isDebug()) {
                if (result instanceof MessageHandlerResult.Invalid invalid) {
                    logger.warn("Message {} is not valid: " + invalid.message(), context.message().toString());
                } else if (result instanceof MessageHandlerResult.Ignored) {
                    logger.warn("Message {} has been ignored", context.message().toString());
                }
            }
        });
    }   

    public void handle(@NotNull Collection<SocialUser> recipients, @NotNull SocialHandlerContext context) {
        recipients.forEach(recipient -> handle(recipient, context));
    }

}
