package ovh.mythmc.social.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class SystemMessagesConfig {

    @Comment("Whether system messages should be enabled or disabled")
    private boolean enabled = true;

    private String joinMessage = "<green>+</green> <white>@nickname</white>";

    private String quitMessage = "<red>-</red> <white>@nickname</white>";
}
