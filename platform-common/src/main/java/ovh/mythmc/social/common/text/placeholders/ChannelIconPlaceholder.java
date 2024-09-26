package ovh.mythmc.social.common.text.placeholders;

import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialPlaceholder;

public final class ChannelIconPlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "channel_icon";
    }

    @Override
    public String process(SocialPlayer player) {
        return player.getMainChannel().getIcon();
    }

}
