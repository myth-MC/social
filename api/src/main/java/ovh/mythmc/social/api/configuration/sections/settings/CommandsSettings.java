package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CommandsSettings {

    @Comment("/pm")
    private PrivateMessageCommand privateMessage = new PrivateMessageCommand(true, "<dark_gray>[<green>:envelope:</green>]</dark_gray>", "<gray>:arrow:</gray>", "<gray>This message is sent to you through a <green>private channel</green></gray>");

    @Comment("/reaction")
    private SimpleCommand reaction = new SimpleCommand(true);

    public record PrivateMessageCommand(boolean enabled,
                                        String prefix,
                                        String arrow,
                                        String hoverText) { }

    public record SimpleCommand(boolean enabled) { }

}
