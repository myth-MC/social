package ovh.mythmc.social.common.placeholders.impl;

import ovh.mythmc.social.api.text.SocialPlaceholder;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class ChannelPlaceholderImpl extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "@channel";
    }

    @Override
    public String process(SocialPlayer player) {
        String hexColor = "<color:" + player.getMainChannel().getIconColor().asHexString().toUpperCase() + ">";
        return hexColor + player.getMainChannel().getName() + "</color>";
    }

}
