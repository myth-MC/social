package ovh.mythmc.social.common.command.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
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
                final var previousChannel = ctx.sender().mainChannel();
                final var privateChannel = PrivateChatChannel.getOrCreate(ctx.sender(), optionalRecipient.get());

                Social.get().getUserManager().setMainChannel(ctx.sender(), privateChannel, false);
                PlatformAdapter.get().sendChatMessage(ctx.sender(), message);
                Social.get().getUserManager().setMainChannel(ctx.sender(), previousChannel, false);

                // Set latest private message recipient (necessary for the /reply command to work)
                Social.get().getUserManager().setLatestPrivateMessageRecipient(ctx.sender(), optionalRecipient.get());
                Social.get().getUserManager().setLatestPrivateMessageRecipient(optionalRecipient.get(), ctx.sender());
            })
        );
    }

}
