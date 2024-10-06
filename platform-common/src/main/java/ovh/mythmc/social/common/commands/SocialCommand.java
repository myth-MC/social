package ovh.mythmc.social.common.commands;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.subcommands.*;

import java.util.*;

public abstract class SocialCommand {

    private final Map<String, SubCommand> subCommands;

    public SocialCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("announcement", new AnnouncementSubcommand());
        subCommands.put("channel", new ChannelSubcommand());
        subCommands.put("nickname", new NicknameSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("socialspy", new SocialSpySubcommand());
    }

    public void run(@NotNull SocialPlayer socialPlayer, @NotNull String[] args) {
        if (args.length == 0) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getNotEnoughArguments(), Social.get().getConfig().getMessages().getChannelType());
            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            Social.get().getTextProcessor().parseAndSend(socialPlayer, Social.get().getConfig().getMessages().getErrors().getInvalidCommand(), Social.get().getConfig().getMessages().getChannelType());
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
