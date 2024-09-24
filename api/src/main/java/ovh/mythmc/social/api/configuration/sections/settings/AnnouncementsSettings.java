package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class AnnouncementsSettings {

    @Comment("Whether announcements should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Announcement frequency in seconds (300 seconds = 5 minutes)")
    private int frequency = 300;

    @Comment("Announcements")
    private List<Announcement> messages = List.of(
            new Announcement(List.of("global", "staff"), "<gray>This is a test announcement!</gray>"),
            new Announcement(List.of("global"), "<gray>This is an <red>exclusive</red> announcement ;-)</gray>")
    );

    public record Announcement(List<String> channels,
                               String message) { }

}
