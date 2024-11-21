package ovh.mythmc.social.common.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;

import ovh.mythmc.social.common.commands.subcommands.social.*;

import java.util.*;

public abstract class SocialCommand {

    private final Map<String, SubCommand> subCommands;

    public SocialCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("announcement", new AnnouncementSubcommand());
        subCommands.put("channel", new ChannelSubcommand());
        subCommands.put("dictionary", new DictionarySubcommand());
        subCommands.put("history", new HistorySubcommand());
        subCommands.put("nickname", new NicknameSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("socialspy", new SocialSpySubcommand());
    }

    public void run(@NotNull CommandSender commandSender, @NotNull String[] args) {
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
                .filter(s -> s.startsWith(args[0]) && commandSender.hasPermission("social.command." + s))
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
