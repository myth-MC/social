package ovh.mythmc.social.api.text.parsers;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.players.SocialPlayer;

@Deprecated
@ScheduledForRemoval
public interface SocialParser {

    Component parse(SocialPlayer socialPlayer, Component message);

}
