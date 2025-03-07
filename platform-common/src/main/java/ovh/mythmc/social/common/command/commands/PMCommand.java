package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.message.SocialPrivateMessageSend;
import ovh.mythmc.social.api.callback.message.SocialPrivateMessageSendCallback;
import ovh.mythmc.social.api.user.AbstractSocialUser;
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
            .commandDescription(Description.of("Sends a private message to another user"))
            .permission("social.use.pm")
            .required("recipient", UserParser.userParser())
            .required("message", StringParser.greedyStringParser())
            .handler(ctx -> {
                final AbstractSocialUser recipient = ctx.get("recipient");
                final String message = ctx.get("message");

                final var callback = new SocialPrivateMessageSend(ctx.sender(), recipient, message);
                SocialPrivateMessageSendCallback.INSTANCE.invoke(callback, result -> {
                    if (!result.cancelled())
                        Social.get().getChatManager().sendPrivateMessage(result.sender(), result.recipient(), result.plainMessage());
                });    
            })
        );
    }
    
}
