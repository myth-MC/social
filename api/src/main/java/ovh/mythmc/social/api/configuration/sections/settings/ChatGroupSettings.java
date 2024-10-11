package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class ChatGroupSettings {

    @Comment("Whether the filter module should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Group channel color (used in some messages)")
    private String color = "";

    @Comment("Group channel icon")
    private String icon = "";

    @Comment("Whether to show text when hovering over the icon or not")
    private boolean showHoverText = true;

    @Comment("Text that will be shown when hovering over the icon")
    private List<String> hoverText = List.of("");

    @Comment("Group nickname color")
    private String nicknameColor = "";

    @Comment("Character that delimits the actual player's message")
    private String textDivider = "";

    @Comment("Group text color")
    private String textColor = "";

}
