package ovh.mythmc.social.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class SystemMessagesConfig {

    @Comment("Whether customized system messages should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Message that will be sent to everyone when a player joins the server")
    private String joinMessage = "<green>+</green> <white>@nickname</white>";

    @Comment("Message that will be sent to everyone when a player quits the server")
    private String quitMessage = "<red>-</red> <white>@nickname</white>";
}
