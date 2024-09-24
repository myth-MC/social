package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.messages.CommandsMessages;
import ovh.mythmc.social.api.configuration.sections.messages.ErrorsMessages;

@Configuration
@Getter
public class SocialMessages {

    @Comment("General errors")
    public ErrorsMessages errors = new ErrorsMessages();

    @Comment({"", "Command messages"})
    public CommandsMessages commands = new CommandsMessages();

}
