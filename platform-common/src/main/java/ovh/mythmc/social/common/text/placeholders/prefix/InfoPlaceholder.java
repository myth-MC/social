package ovh.mythmc.social.common.text.placeholders.prefix;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class InfoPlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "info_prefix";
    }

    @Override
    public String process(SocialPlayer player) {
        return Social.get().getConfig().getMessages().getInfoPrefix();
    }

}
