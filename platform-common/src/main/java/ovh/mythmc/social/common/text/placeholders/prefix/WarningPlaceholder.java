package ovh.mythmc.social.common.text.placeholders.prefix;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialPlaceholder;

public final class WarningPlaceholder extends SocialPlaceholder {

    @Override
    public String identifier() {
        return "warning_prefix";
    }

    @Override
    public String process(SocialPlayer player) {
        return Social.get().getConfig().getMessages().getWarningPrefix();
    }

}
