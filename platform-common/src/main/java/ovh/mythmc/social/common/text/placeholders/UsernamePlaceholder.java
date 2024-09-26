package ovh.mythmc.social.common.text.placeholders;

import ovh.mythmc.social.api.text.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class UsernamePlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "username";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getPlayer().getName();
    }

}
