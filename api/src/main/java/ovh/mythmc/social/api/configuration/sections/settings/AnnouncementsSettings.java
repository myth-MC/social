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

    @Comment("Announcements will be sent in sequential order")
    private List<Announcement> messages = List.of(
            new Announcement("<gray>This is a test announcement! <blue><click:open_url:https://i.ytimg.com/vi/TK4I4RTOjQo/maxresdefault.jpg>Click here ;-)</click></blue>.</gray>", List.of("global")),
            new Announcement("<gray>This is an <red>exclusive</red> announcement</gray>", List.of("staff"))
    );

    @Comment("Enabling this will make announcements show in action bar instead of using chat channels")
    private boolean useActionBar = false;

    public record Announcement(String message,
                               List<String> channels) { }

}
