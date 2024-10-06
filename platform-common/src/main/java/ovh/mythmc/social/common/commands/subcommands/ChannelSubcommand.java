package ovh.mythmc.social.common.commands.subcommands;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class ChannelSubcommand implements SubCommand {

    @Override
    public void accept(SocialPlayer socialPlayer, String[] args) {
        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        ChatChannel channel = Social.get().getChatManager().getChannel(args[0]);
        if (channel == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getChannelDoesNotExist(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (channel == socialPlayer.getMainChannel())
            return;

        if (channel.getPermission() != null && !socialPlayer.getPlayer().hasPermission(channel.getPermission())) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughPermission(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Social.get().getPlayerManager().setMainChannel(socialPlayer, channel);
    }

    @Override
    public List<String> tabComplete(SocialPlayer socialPlayer, String[] args) {
        if (args.length == 1) {
            List<String> channels = new ArrayList<>();
            Social.get().getChatManager().getChannels().forEach(channel -> {
                if (Social.get().getChatManager().hasPermission(socialPlayer, channel))
                    channels.add(channel.getName());
            });

            return channels;
        }

        return List.of();
    }
}
