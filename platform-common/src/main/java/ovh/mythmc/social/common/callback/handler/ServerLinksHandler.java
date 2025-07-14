package ovh.mythmc.social.common.callback.handler;

import java.util.List;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings.ServerLink;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.callback.game.UserPresence;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public final class ServerLinksHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        UserPresenceCallback.INSTANCE.registerListener("social:server-links", (optionalUser, type, optionalMessage) -> {
            if (!type.equals(UserPresence.Type.JOIN))
                return;

            optionalUser.ifPresent(user -> {
                final List<ServerLink> serverLinks = Social.get().getConfig().getServerLinks().getLinks();
                PlatformAdapter.get().sendServerLinks(user, serverLinks);
            });
        });
    }

    @Override
    public void unregister() {
        UserPresenceCallback.INSTANCE.unregisterListeners("social:server-links");
    }
    
}
