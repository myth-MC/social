package ovh.mythmc.social.api.handlers;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.handlers.defaults.ActionBarMessageHandler;
import ovh.mythmc.social.api.handlers.defaults.ChatMessageHandler;
import ovh.mythmc.social.api.handlers.defaults.SystemMessageHandler;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RegisteredMessageHandler<O extends MessageHandlerOptions> {

    private final @NotNull MessageHandler<O> handler;

    private final @NotNull O options;

    protected MessageHandlerResult handle(@NonNull SocialUser user, @NonNull SocialHandlerContext context) {
        boolean handle = options.handleIf().apply(user, context);

        if (!handle)
            return new MessageHandlerResult.Ignored(user, context);

        return handler.handle(user, context, options);
    }

    public static final class Default {

        public static final RegisteredMessageHandler<MessageHandlerOptions> ACTION_BAR = new RegisteredMessageHandler<MessageHandlerOptions>(ActionBarMessageHandler.INSTANCE, new MessageHandlerOptions());

        public static final RegisteredMessageHandler<MessageHandlerOptions> CHAT = new RegisteredMessageHandler<MessageHandlerOptions>(ChatMessageHandler.INSTANCE, new MessageHandlerOptions());

        public static final RegisteredMessageHandler<MessageHandlerOptions> SYSTEM = new RegisteredMessageHandler<MessageHandlerOptions>(SystemMessageHandler.INSTANCE, new MessageHandlerOptions());

    }
    
}
