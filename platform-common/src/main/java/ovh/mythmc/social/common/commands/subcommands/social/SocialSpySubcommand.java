package ovh.mythmc.social.common.commands.subcommands.social;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class SocialSpySubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        SocialPlayer socialPlayer = null;
        if (commandSender instanceof Player player)
            socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());

        if (socialPlayer == null) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        if (!socialPlayer.getPlayer().hasPermission("social.command.socialspy")) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getPlayerManager().setSocialSpy(socialPlayer, !socialPlayer.isSocialSpy());
        Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getCommands().getSocialSpyStatusChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }
}
