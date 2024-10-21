package ovh.mythmc.social.common.commands.subcommands.group;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

public class GroupAliasSubcommand implements SubCommand {
    
    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.alias")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);
        if (chatChannel == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getDoesNotBelongToAGroup(), Social.get().getConfig().getMessages().getChannelType());
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

        String alias = args[0];
        if (alias.length() > 16) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getGroupAliasTooLong(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getChatManager().setGroupChannelAlias(chatChannel, alias);
        Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getGroupAliasChanged(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

}
