package ovh.mythmc.social.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class InteractionMenuConfig {

    @Comment("Whether the interaction menu should be enabled or disabled")
    private boolean enabled = true;

}
