package ovh.mythmc.social.common.callback.handler;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;
import ovh.mythmc.social.common.callback.game.UserPresence.Type;

public final class MOTDHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        UserPresenceCallback.INSTANCE.registerListener("social:motd", (optionalUser, type, optionalMessage) -> {
            if (!type.equals(Type.JOIN))
                return;

            optionalUser.ifPresent(user -> {
                Social.get().getConfig().getMotd().getMessage().forEach(user::sendParsableMessage);
            });
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterListeners("social:motd");
    }
    
}
