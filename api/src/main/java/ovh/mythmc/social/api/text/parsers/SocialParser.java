package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.players.SocialPlayer;

public interface SocialParser {

    Component parse(SocialPlayer socialPlayer, Component message);

}
