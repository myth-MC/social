package ovh.mythmc.social.common.commands.subcommands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
            // unexpected error catch
            return;
        }

        if (!player.getPlayer().hasPermission("social.command.nickname")) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughPermission(), messages.getChannelType());
            return;
        }

        if (args.length == 0) {
            processor.processAndSend(player, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        String nickname = args[0];

        if (nickname.length() > 16) {
            processor.processAndSend(player, messages.getErrors().getNicknameTooLong(), messages.getChannelType());
            return;
        }

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null &&
                    offlinePlayer.getName().equalsIgnoreCase(nickname) &&
                    offlinePlayer.hasPlayedBefore()) {
                processor.processAndSend(player, messages.getErrors().getNicknameAlreadyInUse(), messages.getChannelType());
                return;
            }
        }

        if (args.length == 1) {
            if (nickname.equalsIgnoreCase("reset")) {
                player.getPlayer().setDisplayName(player.getNickname());
                processor.processAndSend(player, messages.getCommands().getNicknameResetted(), messages.getChannelType());
                return;
            }

            player.getPlayer().setDisplayName(nickname);
            processor.processAndSend(player, messages.getCommands().getNicknameChanged(), messages.getChannelType());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            processor.processAndSend(player, messages.getErrors().getPlayerNotFound(), messages.getChannelType());
            return;
        }

        if (nickname.equalsIgnoreCase("reset")) {
            target.setDisplayName(target.getName());
            processor.processAndSend(player, String.format(messages.getCommands().getNicknameResettedOthers(), target.getName()), messages.getChannelType());
            return;
        }

        target.setDisplayName(nickname);
        processor.processAndSend(player, String.format(messages.getCommands().getNicknameChangedOthers(), target.getName(), nickname), messages.getChannelType());
    }

}
