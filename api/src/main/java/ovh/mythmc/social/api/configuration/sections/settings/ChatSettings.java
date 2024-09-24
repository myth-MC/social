package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class ChatSettings {

    @Comment("Whether the chat module should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Default channel that every player should have access to when they join")
    private String defaultChannel = "global";

    @Comment("Text that appears when hovering over a message's channel icon")
    private String channelHoverText = "<gray>Click here to switch to channel <gold>@channel</gold></gray>";

    @Comment("Text that appears when hovering over a player's name")
    private String playerHoverText = "<gray>Click here to message <blue>@nickname</blue></gray>";

    @Comment("Text that appears when hovering over a player's name when they have a nickname")
    private String playerAliasWarningHoverText = "<gold>Warning:</gold> <gray>this player is using an alias</gray>";

    @Comment("You can add placeholders such as the player's rank")
    private String playerNicknameFormat = "@nickname";

    @Comment("Add or remove channels according to your server's needs")
    private List<Channel> channels = List.of(
            new Channel("system", "ACTION_BAR", null, "S", "#FFFFFF", List.of(), "#FFFFFF", "▶", "#FFFFFF", true),
            new Channel("global", "CHAT", null, "✎", "#FFA500", List.of("This is the global channel"), "#D3D3D3", "▶", "#FFFFFF", true),
            new Channel("staff", "CHAT", "social.admin", "✎", "#FF5555", List.of("This channel is restricted to staff members"), "#FFFF55", "▶", "#FFFFFF", true)
    );

    public record Channel(String name,
                          String type,
                          String permission,
                          String icon,
                          String iconColor,
                          List<String> hoverText,
                          String nicknameColor,
                          String textSeparator,
                          String textColor,
                          boolean joinByDefault) { }

}
