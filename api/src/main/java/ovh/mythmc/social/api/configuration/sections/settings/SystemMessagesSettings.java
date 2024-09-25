package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class SystemMessagesSettings {

    @Comment("Whether customized system messages should be enabled or disabled")
    private boolean enabled = true;

    @Comment({"Channel type", "Can be CHAT or ACTION_BAR"})
    private String channelType = "CHAT";

    @Comment("Message that will be sent to everyone when a player joins the server")
    private String joinMessage = "<green>+</green> <white>@nickname</white>";

    @Comment("Message that will be sent to everyone when a player quits the server")
    private String quitMessage = "<red>-</red> <white>@nickname</white>";

    @Comment({"Message that will be sent to everyone when a player dies", "%s = Vanilla death message"})
    private String deathMessage = "<white>:skull:</white> %s";

}
