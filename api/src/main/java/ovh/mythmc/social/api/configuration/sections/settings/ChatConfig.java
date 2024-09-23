package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class ChatConfig {

    @Comment("Whether the chat module should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Default channel that every player should have access to when they join")
    private String defaultChannel = "global";

    @Comment("Text that appears when hovering over a message's channel icon")
    private String channelHoverText = "<gray>Click here to switch to channel <gold>@channel</gold></gray>";

    @Comment("Text that appears when hovering over a player's name")
    private String playerHoverText = "<gray>Click here to message <blue>@username</blue></gray>";

    @Comment("You can add placeholders such as the player's rank")
    private String playerNicknameFormat = "@nickname";

    @Comment("Add or remove channels according to your server's needs")
    private List<Channel> channels = List.of(
            new Channel("global", null, "✏", "#FFA500", List.of("This is the global channel"), "#555555", "▶", "#FFFFFF"),
            new Channel("staff", "social.admin", "✏", "#FF0000", List.of("This channel is restricted to staff members"), "#CC338B", "▶", "#FFFFFF"),
            new Channel("example", "example.permission", "E", "#FFFFFF", List.of("You can use <blue>MiniMessage</blue> too!"), "#FF00FF", "▶", "#FFFFFF")
    );

    public record Channel(String name,
                          String permission,
                          String icon,
                          String iconColor,
                          List<String> hoverText,
                          String nicknameColor,
                          String textSeparator,
                          String textColor) { }

}
