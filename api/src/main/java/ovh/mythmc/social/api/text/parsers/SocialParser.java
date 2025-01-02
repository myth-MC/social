package ovh.mythmc.social.api.text.parsers;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.users.SocialUser;

@Deprecated
@ScheduledForRemoval
public interface SocialParser {

    Component parse(SocialUser socialPlayer, Component message);

}
