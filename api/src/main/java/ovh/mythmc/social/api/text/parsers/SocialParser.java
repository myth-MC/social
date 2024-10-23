package ovh.mythmc.social.api.text.parsers;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialPlayerContext;
import ovh.mythmc.social.api.players.SocialPlayer;

public interface SocialParser {

    default Component parse(SocialPlayerContext context) {
        return parse(context.socialPlayer(), context.message());
    }
    
    @Deprecated
    @ScheduledForRemoval
    default Component parse(SocialPlayer socialPlayer, Component component) {
        return null;
    }

}
