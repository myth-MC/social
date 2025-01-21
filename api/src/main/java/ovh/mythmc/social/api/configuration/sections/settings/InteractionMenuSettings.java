package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class InteractionMenuSettings {

    @Comment("Whether the interaction menu should be enabled")
    private boolean enabled = true;

}
