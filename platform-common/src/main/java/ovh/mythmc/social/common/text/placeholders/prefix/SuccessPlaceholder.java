package ovh.mythmc.social.common.text.placeholders.prefix;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class SuccessPlaceholder extends SocialPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "success_prefix";
    }

    @Override
    public String process(SocialPlayer player) {
        return Social.get().getConfig().getMessages().getSuccessPrefix();
    }

}
