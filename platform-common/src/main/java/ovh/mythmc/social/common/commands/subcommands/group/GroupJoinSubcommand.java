package ovh.mythmc.social.common.commands.subcommands.group;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.events.groups.SocialGroupJoinEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class GroupJoinSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.join")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        if (Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer) != null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getAlreadyBelongsToAGroup(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length < 1) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Integer code = tryParse(args[0]);
        if (code == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getInvalidNumber(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByCode(code);
        if (chatChannel == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getGroupDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (chatChannel.addMember(socialPlayer)) {
            SocialGroupJoinEvent socialGroupJoinEvent = new SocialGroupJoinEvent(chatChannel, socialPlayer);
            Bukkit.getPluginManager().callEvent(socialGroupJoinEvent);

            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getJoinedGroup(), Social.get().getConfig().getMessages().getChannelType());
            Social.get().getPlayerManager().setMainChannel(socialPlayer, chatChannel);
        } else {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getGroupIsFull(), Social.get().getConfig().getMessages().getChannelType());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
