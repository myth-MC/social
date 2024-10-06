package ovh.mythmc.social.common.commands.subcommands;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class SocialSpySubcommand implements SubCommand {

    @Override
    public void accept(SocialPlayer socialPlayer, String[] args) {
        if (!socialPlayer.getPlayer().hasPermission("social.command.socialspy")) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getPlayerManager().setSocialSpy(socialPlayer, !socialPlayer.isSocialSpy());
        Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getCommands().getSocialSpyStatusChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(SocialPlayer socialPlayer, String[] args) {
        return List.of();
    }
}
