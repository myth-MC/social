package ovh.mythmc.social.common.commands;

import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.List;
import java.util.function.BiConsumer;

public interface SubCommand extends BiConsumer<SocialPlayer, String[]> {

    List<String> tabComplete(SocialPlayer socialPlayer, String[] args);

}
