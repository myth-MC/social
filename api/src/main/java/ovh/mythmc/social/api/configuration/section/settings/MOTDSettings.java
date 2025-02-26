package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class MOTDSettings {

    @Comment("Whether the MOTD module should be enabled")
    private boolean enabled = true;

    @Comment("Message that will be sent to player when they join")
    private List<String> message = List.of("<yellow>:smile:</yellow> <white>Welcome back, <blue>$(nickname)</blue>!</white>");

}
