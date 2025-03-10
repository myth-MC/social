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

public final class ReplyCommand implements MainCommand<AbstractSocialUser> {

    @Override
    public boolean canRegister() {
        return Social.get().getConfig().getCommands().getPrivateMessage().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<AbstractSocialUser> commandManager) {
        final Command.Builder<AbstractSocialUser> replyCommand = commandManager.commandBuilder("reply", "r", "re");

        commandManager.command(replyCommand
            .commandDescription(Description.of("Replies to the latest private message"))
            .permission("social.use.reply")
            .required("message", StringParser.greedyStringParser())
            .handler(ctx -> {
                final var optionalRecipient = ctx.sender().latestPrivateMessageRecipient();
                if (optionalRecipient.isEmpty()) {
                    Social.get().getTextProcessor().parseAndSend(ctx.sender(), Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(), Social.get().getConfig().getMessages().getChannelType());
                    return;
                }

                final String message = ctx.get("message");
                final var callback = new SocialPrivateMessageSend(ctx.sender(), optionalRecipient.get(), message);
                SocialPrivateMessageSendCallback.INSTANCE.invoke(callback, result -> {
                    if (!result.cancelled())
                        Social.get().getChatManager().sendPrivateMessage(result.sender(), result.recipient(), result.plainMessage());
                });
            })
        );
    }

}
