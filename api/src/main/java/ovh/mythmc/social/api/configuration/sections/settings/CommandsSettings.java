package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsSettings {

    @Comment("/pm")
    private PrivateMessageCommand privateMessage = new PrivateMessageCommand(true, "<gray>This message is sent to you through a <green>private channel</green></gray>");

    public record PrivateMessageCommand(boolean enabled,
                                        String hoverText) { }

}