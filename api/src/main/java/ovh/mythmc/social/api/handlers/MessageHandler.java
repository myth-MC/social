package ovh.mythmc.social.api.handlers;

import lombok.NonNull;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.users.SocialUser;

public abstract class MessageHandler<O extends MessageHandlerOptions> {
    
    protected abstract MessageHandlerResult handle(@NonNull SocialUser recipient, @NonNull SocialHandlerContext context, @NonNull O options);

    static <O extends MessageHandlerOptions> MessageHandler<O> from(@NonNull IHandler<O> handler) {
        return new MessageHandler<O>() {

            @Override
            protected MessageHandlerResult handle(@NonNull SocialUser recipient, @NonNull SocialHandlerContext context,
                    @NonNull O options) {

                return handler.get(recipient, context, options);
            }
            
        };
    }

    @FunctionalInterface
    public interface IHandler<O extends MessageHandlerOptions> {

        MessageHandlerResult get(@NonNull SocialUser recipient, @NonNull SocialHandlerContext context, @NonNull O options);

    }

}
