package ovh.mythmc.social.common.command.commands;

import java.util.Optional;
import java.util.UUID;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.user.InGameSocialUser;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.Mutable;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.command.MainCommand;

public final class ReplyCommand implements MainCommand<SocialUser> {

    @Override
    public boolean canRegister() {
        return Social.get().getConfig().getCommands().getPrivateMessage().enabled();
    }

    @Override
    public void register(@NotNull CommandManager<SocialUser> commandManager) {
        final Command.Builder<SocialUser> replyCommand = commandManager.commandBuilder("reply", "r", "re");

        commandManager.command(replyCommand
                .commandDescription(Description.of("Replies to the latest private message"))
                .permission("social.use.reply")
                .required("message", StringParser.greedyStringParser(), Description.of("The message that will be replied"))
                .senderType(InGameSocialUser.class)
                .handler(ctx -> {
                    final Mutable<UUID> recipientUuid = ctx.sender().lastPrivateMessageRecipient();
                    if (recipientUuid.isEmpty()) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    final Optional<SocialUser> optionalUser = Social.get().getUserService()
                            .getByUuid(recipientUuid.get());
                    if (optionalUser.isEmpty()) {
                        Social.get().getTextProcessor().parseAndSend(ctx.sender(),
                                Social.get().getConfig().getMessages().getErrors().getPlayerNotFound(),
                                Social.get().getConfig().getMessages().getChannelType());
                        return;
                    }

                    final String message = ctx.get("message");
                    final var previousChannel = ctx.sender().mainChannel().get();
                    final var privateChannel = PrivateChatChannel.getOrCreate(ctx.sender(), optionalUser.get());

                    ctx.sender().mainChannel().set(privateChannel);
                    PlatformAdapter.get().sendChatMessage(ctx.sender(), message);
                    ctx.sender().mainChannel().set(previousChannel);
                }));
    }

}
