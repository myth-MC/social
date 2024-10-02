package ovh.mythmc.social.common.commands;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.subcommands.ChannelSubcommand;
import ovh.mythmc.social.common.commands.subcommands.NicknameSubcommand;
import ovh.mythmc.social.common.commands.subcommands.ReloadSubcommand;
import ovh.mythmc.social.common.commands.subcommands.SocialSpySubcommand;

import java.util.*;

public abstract class SocialCommand {

    private final Map<String, SubCommand> subCommands;

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    public SocialCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("channel", new ChannelSubcommand());
        subCommands.put("nickname", new NicknameSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("socialspy", new SocialSpySubcommand());
    }

    public void run(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
        if (args.length == 0) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            processor.parseAndSend(socialPlayer, messages.getErrors().getInvalidCommand(), messages.getChannelType());
            return;
        }

        command.accept(socialPlayer, Arrays.copyOfRange(args, 1, args.length));
    }

    public @NotNull List<String> tabComplete(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
        List<String> commands = subCommands.keySet().stream()
                .filter(s -> s.startsWith(args[0]) && socialPlayer.getPlayer().hasPermission("social.command." + s))
                .toList();

        if (commands.isEmpty())
            return List.of();

        if (args.length == 1)
            return commands;

        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand != null) {
            return subCommand.tabComplete(socialPlayer, Arrays.copyOfRange(args, 1, args.length));
        } else {
            return List.of();
        }
    }

}
