package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserManager;
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
                    final ChatChannel defaultChannel = Social.get().getChatManager().getCachedOrDefault(ctx.sender());
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
                final SocialUserManager userManager = Social.get().getUserManager();
                final AbstractSocialUser recipient = ctx.get("recipient");

                // Send message
                if (ctx.contains("message")) {
                    final String message = ctx.get("message");;

                    final var privateChannel = PrivateChatChannel.getOrCreate(ctx.sender(), recipient);
                    final var previousChannel = ctx.sender().mainChannel();

                    // Quickly switch channels
                    userManager.setMainChannel(ctx.sender(), privateChannel, false);
                    PlatformAdapter.get().sendChatMessage(ctx.sender(), message);
                    userManager.setMainChannel(ctx.sender(), previousChannel, false);
                    return;
                }

                // Open channel
                final var privateChannel = PrivateChatChannel.getOrCreate(ctx.sender(), recipient);
                userManager.setMainChannel(ctx.sender(), privateChannel, true);
            })
        );
    }
    
}
