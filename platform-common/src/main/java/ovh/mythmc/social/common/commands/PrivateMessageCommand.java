package ovh.mythmc.social.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.*;

public abstract class PrivateMessageCommand {

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty()) {
            // error: command cannot be run from console
            return;
        }

        SocialPlayer messageSender = Social.get().getPlayerManager().get(uuid.get());

        if (!Social.get().getConfig().getSettings().getCommands().getPrivateMessage().enabled()) {
            processor.processAndSend(messageSender, messages.getErrors().getFeatureNotAvailable());
            return;
        }

        if (args.length == 0) {
            processor.processAndSend(messageSender, messages.getErrors().getNotEnoughArguments());
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            processor.processAndSend(messageSender, messages.getErrors().getPlayerNotFound());
            return;
        }

        SocialPlayer messageRecipient = Social.get().getPlayerManager().get(player.getUniqueId());

        if (messageSender == null || messageRecipient == null) {
            // error: unexpected error (socialplayer not registered)
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        SocialPrivateMessageEvent socialPrivateMessageEvent = new SocialPrivateMessageEvent(messageSender, messageRecipient, message);
        Bukkit.getPluginManager().callEvent(socialPrivateMessageEvent);
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            List<String> onlinePlayers = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
            return onlinePlayers;
        }

        return List.of();
    }

}
