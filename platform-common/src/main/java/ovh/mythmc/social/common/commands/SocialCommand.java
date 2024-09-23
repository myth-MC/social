package ovh.mythmc.social.common.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.SocialMessages;
import ovh.mythmc.social.api.text.SocialTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.common.commands.subcommands.ChannelSubcommand;
import ovh.mythmc.social.common.commands.subcommands.NicknameSubcommand;
import ovh.mythmc.social.common.commands.subcommands.ReloadSubcommand;

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
            processor.processAndSend(player, messages.getErrors().getNotEnoughArguments());
            return;
        }

        var command = subCommands.get(args[0]);
        if (command == null) {
            processor.processAndSend(player, messages.getErrors().getInvalidCommand());
            return;
        }

        command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public @NotNull Collection<String> getSuggestions(@NotNull String[] args) {
        if (args.length == 1) {
            return List.copyOf(subCommands.keySet());
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "channel": {
                    List<String> channels = new ArrayList<>();
                    Social.get().getChatManager().getChannels().forEach(channel -> channels.add(channel.getName()));

                    return channels;
                }
            }
        }

        return List.of();
    }

}
