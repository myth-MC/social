package ovh.mythmc.social.common.command;

import java.util.Collection;
import java.util.List;

import org.incendo.cloud.CommandManager;

import lombok.RequiredArgsConstructor;
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
        commands.forEach(command -> {
            if (command.canRegister())
                command.register(commandManager);
        });
    }
    
}
