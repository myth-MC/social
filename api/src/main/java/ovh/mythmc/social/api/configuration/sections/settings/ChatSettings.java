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
    private String channelHoverText = "$(channel_icon) <gray>Click here to switch to channel $(channel)</gray>";

    @Comment("Command that will be suggested when a player clicks on another player's nickname")
    private String clickableNicknameCommand = "pm $(username) ";

    @Comment("Text that appears when hovering over a player's name")
    private String clickableNicknameHoverText = "$(private_message_prefix) <gray>Click here to message <blue>$(nickname)</blue></gray>";

    @Comment("Text that appears when hovering over a player's name when they have a nickname")
    private String playerAliasWarningHoverText = "$(warning_prefix) <gray>This player is using an alias. Their real username is <blue>$(username)</blue></gray>";

    @Comment("You can add placeholders such as the player's rank")
    private String playerNicknameFormat = "$(clickable_nickname)";

    @Comment("Message that appears when a player replies to a message")
    private String replyFormat = "<blue>:raw_box_up_and_right:</blue>";

    @Comment("Text that appears when hovering over a message reply")
    private String replyHoverText = "<dark_gray>Click here to reply to <gray>$(nickname)</gray>'s message</dark_gray>";

    @Comment("Add or remove channels according to your server's needs")
    private List<Channel> channels = List.of(
            new Channel("global", "#FFFF55", null, "<dark_gray>[<yellow>:raw_pencil:</yellow>]</dark_gray>", true, List.of("This is the global channel"), "#D3D3D3", "<gray>:raw_divider:</gray>", "#FFFFFF", true),
            new Channel("staff", "#FF5555", "social.admin", "<dark_gray>[<red>:raw_pencil:</red>]</dark_gray>", true, List.of("This channel is restricted to staff members"), "#FFFF55", "<gray>:raw_divider:</gray>", "#FFFFFF", true)
    );

    @Comment("Whether mentions should be enabled or disabled")
    private boolean mentions = true;

    @Comment("Sound that will be played to the mentioned player")
    private String mentionSound = "BLOCK_CHAIN_PLACE";

    @Comment("Message that the player who has been mentioned will see when hovering over his own nickname")
    private String mentionHoverText = "<dark_gray>[<green>:raw_music:</green>]</dark_gray> <gray><blue>$(nickname)</blue> has mentioned you!</gray>";

    @Comment("Groups module")
    private ChatGroupSettings groups = new ChatGroupSettings();

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
