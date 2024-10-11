package ovh.mythmc.social.common.commands.subcommands.social;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class NicknameSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.nickname")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        String nickname = args[0];

        if (nickname.length() > 16) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNicknameTooLong(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null &&
                    //!offlinePlayer.getName().equalsIgnoreCase(socialPlayer.getPlayer().getName()) && // Nickname belongs to sender
                    offlinePlayer.getName().equalsIgnoreCase(nickname) &&
                    offlinePlayer.hasPlayedBefore()) {
                Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNicknameAlreadyInUse(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }
        }

        if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                return;
            }

            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
            if (socialPlayer == null)
                return;

            if (nickname.equalsIgnoreCase("reset")) {
                socialPlayer.getPlayer().setDisplayName(socialPlayer.getPlayer().getName());
                Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getCommands().getNicknameResetted(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            socialPlayer.getPlayer().setDisplayName(nickname);
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getCommands().getNicknameChanged(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (nickname.equalsIgnoreCase("reset")) {
            target.setDisplayName(target.getName());
            Social.get().getTextProcessor().parseAndSend(commandSender, String.format(Social.get().getConfig().getMessages().getCommands().getNicknameResettedOthers(), target.getName()), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        target.setDisplayName(nickname);
        Social.get().getTextProcessor().parseAndSend(commandSender, String.format(Social.get().getConfig().getMessages().getCommands().getNicknameChangedOthers(), target.getName(), nickname), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return List.of("reset");
        } else if (args.length == 2) {
            List<String> onlinePlayers = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
            return onlinePlayers;
        }

        return List.of();
    }

}
