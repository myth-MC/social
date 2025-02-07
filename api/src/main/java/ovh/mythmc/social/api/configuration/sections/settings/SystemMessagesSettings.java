package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class SystemMessagesSettings {

    @Comment("Whether customized system messages should be enabled")
    private boolean enabled = true;

    @Comment({"Channel type", "Can be CHAT or ACTION_BAR"})
    private String channelType = "CHAT";

    @Comment("Whether the join message should be customized or not")
    private boolean customizeJoinMessage = true;

    @Comment("Message that will be sent to everyone when a player joins the server")
    private String joinMessage = "<dark_gray>[<green>+</green>]</dark_gray> <white>$(clickable_nickname)</white>";

    @Comment("Delay in TICKS between the join event and the join message")
    private int joinMessageDelayInTicks = 1;

    @Comment("Whether the quit message should be customized or not")
    private boolean customizeQuitMessage = true;

    @Comment("Message that will be sent to everyone when a player quits the server")
    private String quitMessage = "<dark_gray>[<red>-</red>]</dark_gray> <white>$(clickable_nickname)</white>";

    @Comment("Delay in TICKS between the quit event and the quit message")
    private int quitMessageDelayInTicks = 1;

    @Comment("Whether the death message should be customized or not")
    private boolean customizeDeathMessage = true;

    @Comment({"Message that will be sent to everyone when a player dies", "%s = Vanilla death message"})
    private String deathMessage = "<dark_gray>[<white>:skull:</white>]</dark_gray> %s";

}
