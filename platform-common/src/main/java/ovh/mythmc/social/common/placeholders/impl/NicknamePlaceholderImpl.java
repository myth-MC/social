package ovh.mythmc.social.common.placeholders.impl;

import ovh.mythmc.social.api.placeholders.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class NicknamePlaceholderImpl implements SocialPlaceholder {

    @Override
    public String identifier() {
        return "@nickname";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getPlayer().getDisplayName();
    }

}
