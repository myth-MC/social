package ovh.mythmc.social.common.commands.subcommands;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class ReloadSubcommand implements SubCommand {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @Override
    public void accept(SocialPlayer socialPlayer, String[] args) {
        if (!socialPlayer.getPlayer().hasPermission("social.command.reload")) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughPermission(), messages.getChannelType());
            return;
        }

        Social.get().reload();
        processor.parseAndSend(socialPlayer, messages.getCommands().getPluginReloaded(), messages.getChannelType());
        processor.parseAndSend(socialPlayer, messages.getCommands().getPluginReloadedModulesWarning(), messages.getChannelType());
    }

    @Override
    public List<String> tabComplete(SocialPlayer socialPlayer, String[] args) {
        return List.of();
    }
}
