package ovh.mythmc.social.api.handlers.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.handlers.MessageHandler;
import ovh.mythmc.social.api.handlers.MessageHandlerOptions;
import ovh.mythmc.social.api.handlers.MessageHandlerResult;
import ovh.mythmc.social.api.users.SocialUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessageHandler extends MessageHandler<MessageHandlerOptions> {

    public static final ChatMessageHandler INSTANCE = new ChatMessageHandler();

    @Override
    protected MessageHandlerResult handle(@NonNull SocialUser recipient, @NonNull SocialHandlerContext context,
            @NonNull MessageHandlerOptions options) {

        if (context.message() == Component.empty())
            return new MessageHandlerResult.Invalid(recipient, context, "Message is empty!");

        if (recipient == null || recipient.getPlayer() == null)
            return new MessageHandlerResult.Invalid(recipient, context, "Recipient is not a valid Player!");

        recipient.sendMessage(context.message());
        return new MessageHandlerResult.Valid(recipient, context);
    }
    
}
