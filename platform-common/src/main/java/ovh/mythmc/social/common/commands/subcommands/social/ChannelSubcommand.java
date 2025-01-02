package ovh.mythmc.social.common.commands.subcommands.social;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class ChannelSubcommand implements SubCommand {

    @Override
    public void accept(CommandSender commandSender, String[] args) {
        if (!commandSender.hasPermission("social.command.channel")) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (!(commandSender instanceof Player)) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        ChatChannel channel = Social.get().getChatManager().getChannel(args[0]);
        if (channel == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialUser socialPlayer = Social.get().getPlayerManager().get(((Player) commandSender).getUniqueId());
        if (socialPlayer == null)
            return;

        if (channel instanceof GroupChatChannel && !channel.getMembers().contains(socialPlayer.getUuid())) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (channel == socialPlayer.getMainChannel())
            return;

        if (channel.getPermission() != null && !commandSender.hasPermission(channel.getPermission())) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getPlayerManager().setMainChannel(socialPlayer, channel);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        SocialUser socialPlayer = null;
        if (commandSender instanceof Player player)
            socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());

        if (socialPlayer == null) {
            return List.of();
        }

        if (args.length == 1) {
            List<String> channels = new ArrayList<>();
            for (ChatChannel channel : Social.get().getChatManager().getChannels()) {
                if (channel instanceof GroupChatChannel) continue;
                if (Social.get().getChatManager().hasPermission(socialPlayer, channel))
                    channels.add(channel.getName());
            }

            return channels;
        }

        return List.of();
    }
}
