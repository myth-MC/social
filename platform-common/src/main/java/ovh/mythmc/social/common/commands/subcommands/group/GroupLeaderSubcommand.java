package ovh.mythmc.social.common.commands.subcommands.group;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupLeaderSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.leader")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);

        if (chatChannel == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getDoesNotBelongToAGroup(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (!chatChannel.getLeaderUuid().equals(socialPlayer.getUuid())) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length < 1) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !chatChannel.getMembers().contains(target.getUniqueId())) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (target.getUniqueId().equals(socialPlayer.getUuid())) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getChooseAnotherPlayer(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getChatManager().setGroupChannelLeader(chatChannel, target.getUniqueId());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player))
            return List.of();

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
            if (groupChatChannel == null)
                return List.of();

            groupChatChannel.getMembers().forEach(uuid -> {
                players.add(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
            });

            return players;
        }

        return List.of();
    }

}
