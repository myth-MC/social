package ovh.mythmc.social.common.callback.handler;

import java.util.regex.Pattern;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.common.callback.game.UserChatCallback;

// Todo: migrate to SocialMessageSendCallback?
public final class ReactionHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        UserChatCallback.INSTANCE.registerListener("social:reaction-trigger", (user, message, cancelled) -> {
            Reaction reaction = null;
            for (Reaction r : Social.get().getReactionManager().getReactionsMap().keySet()) {
                if (r.triggerWords() == null || r.triggerWords().isEmpty())
                    continue;

                for (String triggerWord : r.triggerWords()) {
                    if (message.matches("(?i:" + Pattern.quote(triggerWord) + ")")) {
                        reaction = r;
                    }
                }
            }

            if (reaction != null) {
                user.playReaction(reaction);
            }
        });
    }

    @Override
    public void unregister() {
        UserChatCallback.INSTANCE.unregisterListeners("social:reaction-trigger");
    }
    
}
