package ovh.mythmc.social.common.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.CommandManager;

import lombok.RequiredArgsConstructor;
import org.incendo.cloud.caption.CaptionProvider;
import ovh.mythmc.social.api.user.AbstractSocialUser;
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
    
}
