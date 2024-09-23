package ovh.mythmc.social.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ReloadSubcommand implements BiConsumer<Audience, String[]> {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    @Override
    public void accept(Audience sender, String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty())
            return;

        SocialPlayer player = Social.get().getPlayerManager().get(uuid.get());
        if (player == null) {
            processor.processAndSend(player, messages.getErrors().getUnexpectedError());
            return;
        }

        if (!player.getPlayer().hasPermission("social.command.reload")) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughPermission());
            return;
        }

        Social.get().reload();
        processor.processAndSend(player, messages.getCommands().getPluginReloaded());
    }

}
