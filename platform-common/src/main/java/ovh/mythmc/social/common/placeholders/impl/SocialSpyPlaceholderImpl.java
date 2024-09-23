package ovh.mythmc.social.common.placeholders.impl;

import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialPlaceholder;

public final class SocialSpyPlaceholderImpl extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "@placeholder";
    }

    @Override
    public String process(SocialPlayer player) {
        if (player.isSocialSpy()) {
            return "<green>true</green>";
        }

        return "<red>false</red>";
    }

}
