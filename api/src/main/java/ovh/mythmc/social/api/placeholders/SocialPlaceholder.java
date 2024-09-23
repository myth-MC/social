package ovh.mythmc.social.api.placeholders;

import ovh.mythmc.social.api.players.SocialPlayer;

public interface SocialPlaceholder {

    String identifier();

    String process(SocialPlayer player);

}
