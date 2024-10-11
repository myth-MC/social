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

    @Comment("Player limit per group")
    private int playerLimit = 8;

    @Comment("Text that will be shown when a player hovers over the channel's code")
    private String codeHoverText = "<yellow>:raw_pencil:</yellow> <gray>Click here to copy to clipboard</gray>";

    @Comment("Group channel color (used in some messages)")
    private String color = "#AAAAAA";

    @Comment("Group channel icon")
    private String icon = "<dark_gray>[<gray>:raw_shield:</gray>]</dark_gray>";

    @Comment("Whether to show text when hovering over the icon or not")
    private boolean showHoverText = true;

    @Comment("Text that will be shown when hovering over the icon")
    private List<String> hoverText = List.of("", "<yellow>:sword:</yellow> <gray>Leader: <white>$group_leader</white></gray>", "<dark_gray>This is a group channel.</dark_gray>");

    @Comment("Group nickname color")
    private String nicknameColor = "#FFFFFF";

    @Comment("Character that delimits the actual player's message")
    private String textDivider = "<gray>:raw_play:</gray>";

    @Comment("Group text color")
    private String textColor = "#FFFFFF";

}
