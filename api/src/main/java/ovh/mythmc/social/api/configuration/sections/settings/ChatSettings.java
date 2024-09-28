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
    private String channelHoverText = "@channel_icon <gray>Click here to switch to channel @channel</gray>";

    @Comment("Text that appears when hovering over a player's name")
    private String clickableNicknameHoverText = "@private_message_prefix <gray>Click here to message <blue>@nickname</blue></gray>";

    @Comment("Text that appears when hovering over a player's name when they have a nickname")
    private String playerAliasWarningHoverText = "@warning_prefix <gray>This player is using an alias</gray>";

    @Comment("You can add placeholders such as the player's rank")
    private String playerNicknameFormat = "@clickable_nickname";

    @Comment("Add or remove channels according to your server's needs")
    private List<Channel> channels = List.of(
            new Channel("global", "#FFFF55", null, "<dark_gray>[<yellow>:pencil:</yellow>]</dark_gray>", true, List.of("This is the global channel"), "#D3D3D3", "▶", "#FFFFFF", true),
            new Channel("staff", "#FF5555", "social.admin", "<dark_gray>[<red>:pencil:</red>]</dark_gray>", true, List.of("This channel is restricted to staff members"), "#FFFF55", "▶", "#FFFFFF", true)
    );

    @Comment("Filter module")
    private ChatFilterSettings filter = new ChatFilterSettings();

    public record Channel(String name,
                          String color,
                          String permission,
                          String icon,
                          boolean showHoverText,
                          List<String> hoverText,
                          String nicknameColor,
                          String textDivider,
                          String textColor,
                          boolean joinByDefault) { }

}
