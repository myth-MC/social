package ovh.mythmc.social.common.placeholders.impl;

import ovh.mythmc.social.api.placeholders.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class ChannelPlaceholderImpl implements SocialPlaceholder {

    @Override
    public String identifier() {
        return "@channel";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getMainChannel().getName();
    }

}
