package ovh.mythmc.social.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class NicknameSubcommand implements BiConsumer<Audience, String[]> {

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

        if (!player.getPlayer().hasPermission("social.command.nickname")) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughPermission());
            return;
        }

        if (args.length == 0) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughArguments());
            return;
        }

        String nickname = args[0];
        if (nickname.equalsIgnoreCase("reset")) {
            player.getPlayer().setDisplayName(player.getNickname());
            processor.processAndSend(player, messages.getCommands().getNicknameResetted());
            return;
        }

        if (Bukkit.getOfflinePlayer(nickname).hasPlayedBefore()) {
            processor.processAndSend(player, messages.getErrors().getNicknameAlreadyInUse());
            return;
        }

        if (nickname.length() > 16) {
            processor.processAndSend(player, messages.getErrors().getNicknameTooLong());
            return;
        }

        player.getPlayer().setDisplayName(nickname);
        processor.processAndSend(player, messages.getCommands().getNicknameChanged());
    }

}
