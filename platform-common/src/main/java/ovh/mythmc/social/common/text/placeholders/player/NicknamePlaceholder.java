package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.text.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class NicknamePlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "nickname";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getNickname();
    }

}
