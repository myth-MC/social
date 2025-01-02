package ovh.mythmc.social.common.commands.subcommands.group;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class GroupLeaveSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.leave")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        if (Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer) == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getDoesNotBelongToAGroup(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
        if (chatChannel.getLeaderUuid().equals(socialPlayer.getUuid())) {
            if (chatChannel.getMembers().size() < 2) {
                Social.get().getChatManager().unregisterChatChannel(chatChannel);
                Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getGroupDisbanded(), Social.get().getConfig().getMessages().getChannelType());
                return;
            }

            Social.get().getChatManager().setGroupChannelLeader(chatChannel, chatChannel.getMembers().get(1));
        }

        Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getLeftGroup(), Social.get().getConfig().getMessages().getChannelType());
        chatChannel.removeMember(socialPlayer);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

}
