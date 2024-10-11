package ovh.mythmc.social.common.commands.subcommands.group;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.events.groups.SocialGroupCreateEvent;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.List;

public class GroupCreateSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.group.create")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());

        if (Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer) != null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getAlreadyBelongsToAGroup(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getChatManager().registerGroupChatChannel(socialPlayer.getUuid());
        GroupChatChannel chatChannel = Social.get().getChatManager().getGroupChannelByPlayer(socialPlayer);

        SocialGroupCreateEvent socialGroupCreateEvent = new SocialGroupCreateEvent(chatChannel);
        Bukkit.getPluginManager().callEvent(socialGroupCreateEvent);

        Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getCommands().getCreatedGroup(), Social.get().getConfig().getMessages().getChannelType());
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

}
