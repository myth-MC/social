package ovh.mythmc.social.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ChannelSubcommand implements BiConsumer<Audience, String[]> {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @Override
    public void accept(Audience sender, String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty())
            return;

        SocialPlayer player = Social.get().getPlayerManager().get(uuid.get());
        if (player == null) {
            // unexpected error catch
            return;
        }

        if (args.length == 0) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughArguments());
            return;
        }

        ChatChannel channel = Social.get().getChatManager().getChannel(args[0]);
        if (channel == null) {
            processor.processAndSend(player, messages.getErrors().getChannelDoesNotExist());
            return;
        }

        if (channel == player.getMainChannel())
            return;

        if (channel.getPermission() != null && !player.getPlayer().hasPermission(channel.getPermission())) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughPermission());
            return;
        }

        channel.addMember(uuid.get());
        player.setMainChannel(channel);
        processor.processAndSend(player, messages.getCommands().getChannelChanged());
    }

}
