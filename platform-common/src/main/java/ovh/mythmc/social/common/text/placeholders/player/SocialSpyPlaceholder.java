package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class SocialSpyPlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "socialspy";
    }

    @Override
    public String process(SocialPlayer player) {
        if (player.isSocialSpy()) {
            return "<green>true</green>";
        }

        return "<red>false</red>";
    }

}
