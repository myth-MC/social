package ovh.mythmc.social.common.commands.subcommands;

import org.bukkit.command.CommandSender;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class ReloadSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.reload")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().reload();
        Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getPluginReloaded(), Social.get().getConfig().getMessages().getChannelType());
        Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getPluginReloadedModulesWarning(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }
}
