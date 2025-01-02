package ovh.mythmc.social.common.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.*;

public abstract class PrivateMessageCommand {

    public void run(@NotNull CommandSender commandSender, @NotNull String[] args) {
        SocialUser messageSender = null;
        if (commandSender instanceof Player player)
            messageSender = Social.get().getPlayerManager().get(player.getUniqueId());

        if (messageSender == null) {
            String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
            SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
            return;
        }

        if (!Social.get().getConfig().getSettings().getCommands().getPrivateMessage().enabled()) {
            Social.get().getTextProcessor().parseAndSend(messageSender, messageSender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getFeatureNotAvailable(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(messageSender, messageSender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Social.get().getTextProcessor().parseAndSend(messageSender, messageSender.getMainChannel(), Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        SocialUser messageRecipient = Social.get().getPlayerManager().get(player.getUniqueId());

        if (messageRecipient == null) {
            // error: unexpected error (socialplayer not registered)
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        SocialPrivateMessageEvent socialPrivateMessageEvent = new SocialPrivateMessageEvent(messageSender, messageRecipient, message);
        Bukkit.getPluginManager().callEvent(socialPrivateMessageEvent);
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> onlinePlayers = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
            return onlinePlayers;
        }

        return List.of();
    }

}
