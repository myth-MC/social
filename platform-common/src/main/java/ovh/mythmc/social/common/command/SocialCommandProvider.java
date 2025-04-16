package ovh.mythmc.social.common.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import lombok.RequiredArgsConstructor;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.PredicatePermission;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.command.commands.*;

@RequiredArgsConstructor
public final class SocialCommandProvider {

    private final CommandManager<AbstractSocialUser> commandManager;

    private final Collection<MainCommand<AbstractSocialUser>> commands = List.of(
        new GroupCommand(),
        new PMCommand(),
        new ReactionCommand(),
        new ReplyCommand(),
        new SocialCommand()
    );

    @ApiStatus.Internal
    public void register() {
        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_CHANNEL, "Could not find any channel matching '<input>'")
        );

        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_IDENTIFIED_PARSER, "Could not find any identified parser matching '<input>'")
        );

        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_REACTION, "Could not find any reaction matching '<identifier>' belonging to category '<category>'")
        );

        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_USER, "Could not find any user matching '<input>'")
        );

        commandManager.captionRegistry().registerProvider(
            CaptionProvider.constantProvider(SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_MESSAGE, "Could not find any registered message identified by '<input>'")
        );

        commands.forEach(command -> {
            if (command.canRegister())
                command.register(commandManager);
        });
    }

    public void registerChannelCommand(@NotNull ChatChannel channel) {
        commandManager.command(channelCommandBuilder(commandManager, channel));
    }

    public void unregisterChannelCommand(@NotNull ChatChannel channel) {
        commandManager.deleteRootCommand(channel.name());
    }

    private static Command.Builder<AbstractSocialUser> channelCommandBuilder(@NotNull CommandManager<AbstractSocialUser> commandManager, @NotNull ChatChannel channel) {
        Command.Builder<AbstractSocialUser> commandBuilder;

        if (channel.alias().isPresent()) {
            commandBuilder = commandManager.commandBuilder(channel.name(), channel.alias().get());
        } else {
            commandBuilder = commandManager.commandBuilder(channel.name());
        }

        return commandBuilder
            .commandDescription(Description.of("Switches your main channel to " + channel.name() + " or sends a message without having to switch"))
            .permission(PredicatePermission.of(user -> {
                if (channel.permission().isEmpty())
                    return true;

                return user.checkPermission(channel.permission().get());
            }))
            .optional("message", StringParser.greedyStringParser())
            .handler(ctx -> {
                // Send a message without switching channel
                if (ctx.contains("message")) {
                    // Define variables
                    final String message = ctx.get("message");
                    final ChatChannel previousChannel = ctx.sender().mainChannel();

                    // Quickly switch channel and return after sending message
                    Social.get().getUserManager().setMainChannel(ctx.sender(), channel, false);
                    PlatformAdapter.get().sendChatMessage(ctx.sender(), message);
                    Social.get().getUserManager().setMainChannel(ctx.sender(), previousChannel, false);
                    return;
                }

                // Switch channel
                Social.get().getUserManager().setMainChannel(ctx.sender(), channel, true);
            });
    }
    
}
