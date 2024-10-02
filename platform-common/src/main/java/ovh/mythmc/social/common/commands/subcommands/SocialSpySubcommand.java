package ovh.mythmc.social.common.commands.subcommands;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class SocialSpySubcommand implements SubCommand {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @Override
    public void accept(SocialPlayer socialPlayer, String[] args) {
        if (!socialPlayer.getPlayer().hasPermission("social.command.socialspy")) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughPermission(), messages.getChannelType());
            return;
        }

        Social.get().getPlayerManager().setSocialSpy(socialPlayer, !socialPlayer.isSocialSpy());
        processor.parseAndSend(socialPlayer, messages.getCommands().getSocialSpyStatusChanged(), messages.getChannelType());
    }

    @Override
    public List<String> tabComplete(SocialPlayer socialPlayer, String[] args) {
        return List.of();
    }
}
