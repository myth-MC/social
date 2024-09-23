package ovh.mythmc.social.api.configuration.sections;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class AnnouncementsConfig {

    @Comment("Whether announcements should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Announcement frequency in seconds (900 seconds = 15 minutes)")
    private int frequency = 900;

}
