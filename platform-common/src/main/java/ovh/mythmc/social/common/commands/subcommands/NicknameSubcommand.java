package ovh.mythmc.social.common.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class NicknameSubcommand implements SubCommand {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @Override
    public void accept(SocialPlayer socialPlayer, String[] args) {
        if (!socialPlayer.getPlayer().hasPermission("social.command.nickname")) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughPermission(), messages.getChannelType());
            return;
        }

        if (args.length == 0) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        String nickname = args[0];

        if (nickname.length() > 16) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNicknameTooLong(), messages.getChannelType());
            return;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null &&
                    !offlinePlayer.getName().equalsIgnoreCase(socialPlayer.getPlayer().getName()) && // Nickname belongs to sender
                    offlinePlayer.getName().equalsIgnoreCase(nickname) &&
                    offlinePlayer.hasPlayedBefore()) {
                processor.parseAndSend(socialPlayer, messages.getErrors().getNicknameAlreadyInUse(), messages.getChannelType());
                return;
            }
        }

        if (args.length == 1) {
            if (nickname.equalsIgnoreCase("reset")) {
                socialPlayer.getPlayer().setDisplayName(socialPlayer.getPlayer().getName());
                processor.parseAndSend(socialPlayer, messages.getCommands().getNicknameResetted(), messages.getChannelType());
                return;
            }

            socialPlayer.getPlayer().setDisplayName(nickname);
            processor.parseAndSend(socialPlayer, messages.getCommands().getNicknameChanged(), messages.getChannelType());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getPlayerNotFound(), messages.getChannelType());
            return;
        }

        if (nickname.equalsIgnoreCase("reset")) {
            target.setDisplayName(target.getName());
            processor.parseAndSend(socialPlayer, String.format(messages.getCommands().getNicknameResettedOthers(), target.getName()), messages.getChannelType());
            return;
        }

        target.setDisplayName(nickname);
        processor.parseAndSend(socialPlayer, String.format(messages.getCommands().getNicknameChangedOthers(), target.getName(), nickname), messages.getChannelType());
    }

    @Override
    public List<String> tabComplete(SocialPlayer socialPlayer, String[] args) {
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
