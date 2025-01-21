package ovh.mythmc.social.api.handlers.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.handlers.MessageHandler;
import ovh.mythmc.social.api.handlers.MessageHandlerOptions;
import ovh.mythmc.social.api.handlers.MessageHandlerResult;
import ovh.mythmc.social.api.users.SocialUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemMessageHandler extends MessageHandler<MessageHandlerOptions> {

    public static final SystemMessageHandler INSTANCE = new SystemMessageHandler();

    @Override
    protected MessageHandlerResult handle(@NonNull SocialUser recipient, @NonNull SocialHandlerContext context,
            @NonNull MessageHandlerOptions options) {

        if (Social.get().getConfig().getMessages().isUseActionBar())
            return ActionBarMessageHandler.INSTANCE.handle(recipient, context, options);

        return ChatMessageHandler.INSTANCE.handle(recipient, context, options);
    }

}
