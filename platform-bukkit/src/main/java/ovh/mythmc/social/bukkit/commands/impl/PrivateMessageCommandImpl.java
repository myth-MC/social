package ovh.mythmc.social.bukkit.commands.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.common.commands.PrivateMessageCommand;

import java.util.List;

public final class PrivateMessageCommandImpl extends PrivateMessageCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        run(SocialAdventureProvider.get().getSender(commandSender), args);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return getSuggestions(args).stream().toList();
    }
}
