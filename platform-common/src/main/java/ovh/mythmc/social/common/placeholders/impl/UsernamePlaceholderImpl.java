package ovh.mythmc.social.common.placeholders.impl;

import ovh.mythmc.social.api.text.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class UsernamePlaceholderImpl extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "@username";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getPlayer().getName();
    }

}
