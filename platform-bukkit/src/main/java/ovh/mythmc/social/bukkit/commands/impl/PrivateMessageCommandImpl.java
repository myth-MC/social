package ovh.mythmc.social.bukkit.commands.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.PrivateMessageCommand;

import java.util.List;

public final class PrivateMessageCommandImpl extends PrivateMessageCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());
            if (socialPlayer == null)
                return false;

            run(socialPlayer, args);
            return true;
        }

        String message = Social.get().getConfig().getMessages().getErrors().getCannotBeRunFromConsole();
        SocialAdventureProvider.get().sender(commandSender).sendMessage(MiniMessage.miniMessage().deserialize(message));
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            SocialPlayer socialPlayer = Social.get().getPlayerManager().get(player.getUniqueId());
            if (socialPlayer == null)
                return List.of();

            return tabComplete(socialPlayer, args);
        }

        return List.of();
    }

}
