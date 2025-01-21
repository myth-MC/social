package ovh.mythmc.social.api.handlers;

import java.util.function.BiFunction;

import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.users.SocialUser;

public class MessageHandlerOptions {

    private BiFunction<SocialUser, SocialHandlerContext, Boolean> handleIf = (a, b) -> { return true; };

    private boolean acceptPrivateMessages = false;

    public MessageHandlerOptions handleIf(BiFunction<SocialUser, SocialHandlerContext, Boolean> handleIf) {
        this.handleIf = handleIf;
        return this;
    }

    protected BiFunction<SocialUser, SocialHandlerContext, Boolean> handleIf() {
        return handleIf;
    }

    public MessageHandlerOptions acceptPrivateMessages(boolean acceptPrivateMessages) {
        this.acceptPrivateMessages = acceptPrivateMessages;
        return this;
    }

    protected boolean acceptPrivateMessages() {
        return acceptPrivateMessages;
    }

}