package ovh.mythmc.social.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
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
import java.util.function.BiConsumer;

public abstract class SocialCommand {

    private final Map<String, BiConsumer<Audience, String[]>> subCommands;

    private final SocialTextProcessor processor = Social.get().getTextProcessor();
    private final SocialMessages messages = Social.get().getConfig().getMessages();

    public SocialCommand() {
        this.subCommands = new HashMap<>();
        subCommands.put("channel", new ChannelSubcommand());
        subCommands.put("nickname", new NicknameSubcommand());
        subCommands.put("reload", new ReloadSubcommand());
        subCommands.put("socialspy", new SocialSpySubcommand());
    }

    public void run(@NotNull Audience sender, @NotNull String[] args) {
        Optional<UUID> uuid = sender.get(Identity.UUID);
        if (uuid.isEmpty())
            return;

        SocialPlayer player = Social.get().getPlayerManager().get(uuid.get());
        if (player == null) {
            // error: unexpected error
            return;
        }

        if (args.length == 0) {
            processor.parseAndSend(player, messages.getErrors().getNotEnoughArguments(), messages.getChannelType());
            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            processor.parseAndSend(player, messages.getErrors().getInvalidCommand(), messages.getChannelType());
            return;
        }

        command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            return List.copyOf(subCommands.keySet());
        }

        switch (args[0]) {
            case "channel": {
                if (args.length == 2) {
                    List<String> channels = new ArrayList<>();
                    Social.get().getChatManager().getChannels().forEach(channel -> channels.add(channel.getName()));

                    return channels;
                }
            }
            case "nickname": {
                if (args.length == 2) {
                    return List.of("reset");
                } else if (args.length == 3) {
                    List<String> onlinePlayers = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getName()));
                    return onlinePlayers;
                }
            }
        }

        return List.of();
    }

}
