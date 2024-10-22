package ovh.mythmc.social.common.text.placeholders.prefix;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlaceholder;

public final class PrivateMessagePlaceholder extends SocialPlaceholder {
    
    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "private_message_prefix";
    }

    @Override
    public String process(SocialPlayer player) {
        return Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix();
    }

}
