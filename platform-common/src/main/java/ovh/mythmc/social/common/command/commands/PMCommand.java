package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.PrivateChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.command.MainCommand;
import ovh.mythmc.social.common.command.parser.UserParser;

public final class PMCommand implements MainCommand<AbstractSocialUser> {

    @Override
    public boolean canRegister() {
        return Social.get().getConfig().getCommands().getPrivateMessage().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<AbstractSocialUser> commandManager) {
        final Command.Builder<AbstractSocialUser> pmCommand = commandManager.commandBuilder("pm", "msg", "w", "whisper", "tell");

        commandManager.command(pmCommand
            .handler(ctx -> {
                if (ctx.sender().mainChannel() instanceof PrivateChatChannel) {
                    final ChatChannel defaultChannel = Social.get().getChatManager().getDefaultChannel();
                    Social.get().getUserManager().setMainChannel(ctx.sender(), defaultChannel, true);
                }
            })
        );

        commandManager.command(pmCommand
            .commandDescription(Description.of("Sends a private message to another user"))
            .permission("social.use.pm")
            .required("recipient", UserParser.userParser())
            .optional("message", StringParser.greedyStringParser())
            .handler(ctx -> {
                final AbstractSocialUser recipient = ctx.get("recipient");

                // Send message
                if (ctx.contains("message")) {
                    final String message = ctx.get("message");;

                    final var privateChannel = Social.get().getChatManager().privateChatChannel(ctx.sender(), recipient);
                    final var previousChannel = ctx.sender().mainChannel();

                    // Quickly switch channels
                    Social.get().getUserManager().setMainChannel(ctx.sender(), privateChannel, false);
                    PlatformAdapter.get().sendChatMessage(ctx.sender(), message);
                    Social.get().getUserManager().setMainChannel(ctx.sender(), previousChannel, false);

                    // Set latest private message recipient (necessary for the /reply command to work)
                    Social.get().getUserManager().setLatestPrivateMessageRecipient(ctx.sender(), recipient);

                    return;
                }

                // Open channel
                final var privateChannel = Social.get().getChatManager().privateChatChannel(ctx.sender(), recipient);
                Social.get().getUserManager().setMainChannel(ctx.sender(), privateChannel, true);
            })
        );
    }
    
}
