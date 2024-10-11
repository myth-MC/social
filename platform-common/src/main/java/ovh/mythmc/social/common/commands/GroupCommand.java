package ovh.mythmc.social.common.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.api.features.SocialGestalt;
import ovh.mythmc.social.common.commands.subcommands.group.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GroupCommand {

    private final Map<String, SubCommand> subCommands;

    public GroupCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("create", new GroupCreateSubcommand());
        subCommands.put("chat", new GroupChatSubcommand());
        subCommands.put("join", new GroupJoinSubcommand());
        subCommands.put("leave", new GroupLeaveSubcommand());
        subCommands.put("code", new GroupCodeSubcommand());
        subCommands.put("disband", new GroupDisbandSubcommand());
    }

    public void run(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (!SocialGestalt.get().isEnabled(SocialFeatureType.GROUPS)) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getFeatureNotAvailable(), Social.get().getConfig().getMessages().getChannelType());
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

        var command = subCommands.get(args[0]);
        if (command == null) {
            Social.get().getTextProcessor().parseAndSend(commandSender, Social.get().getConfig().getMessages().getErrors().getInvalidCommand(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        command.accept(commandSender, Arrays.copyOfRange(args, 1, args.length));
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String[] args) {
        List<String> commands = subCommands.keySet().stream()
                .filter(s -> s.startsWith(args[0]) && commandSender.hasPermission("social.command.group." + s))
                .toList();

        if (commands.isEmpty())
            return List.of();

        if (args.length == 1)
            return commands;

        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand != null) {
            return subCommand.tabComplete(commandSender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            return List.of();
        }
    }

}
