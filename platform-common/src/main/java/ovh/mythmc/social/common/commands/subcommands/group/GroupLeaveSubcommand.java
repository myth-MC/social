package ovh.mythmc.social.common.commands.subcommands.group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;
import java.util.UUID;

public class GroupLeaveSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.leave")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        if (Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer) == null) {
            // player does not belong to a group
            return;
        }

        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
        if (chatChannel.getLeaderUuid().equals(socialPlayer.getUuid())) {
            if (chatChannel.getMembers().size() < 2) {
                Social.get().getChatManager().unregisterChatChannel(chatChannel);
                // group has been deleted
                return;
            }

            chatChannel.setLeaderUuid(chatChannel.getMembers().get(1));
        }

        chatChannel.removeMember(socialPlayer);
        // you've left group
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

}
