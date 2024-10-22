package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;
import ovh.mythmc.social.api.configuration.sections.settings.*;

@Configuration
@Getter
public class SocialSettings {

    @Comment("Enabling this will send more logs to console to help debugging")
    private boolean debug = false;

    @Comment("Whether to enable the update checker or not")
    private boolean updateChecker = true;

    @Comment("Time interval of the update checker in hours")
    private int updateCheckerIntervalInHours = 6;

    @Comment({"", "Chat module"})
    private ChatSettings chat = new ChatSettings();

    @Comment({"", "Reactions module"})
    private ReactionsSettings reactions = new ReactionsSettings();

    @Comment({"", "Emojis module"})
    private EmojiSettings emojis = new EmojiSettings();

    @Comment({"", "MOTD module"})
    private MOTDSettings motd = new MOTDSettings();

    @Comment({"", "Announcements module"})
    private AnnouncementsSettings announcements = new AnnouncementsSettings();

    @Comment({"", "System messages module"})
    private SystemMessagesSettings systemMessages = new SystemMessagesSettings();

    @Comment({"", "Packets module"})
    private PacketsSettings packets = new PacketsSettings();

    @Comment({"", "Text replacement module"})
    private TextReplacementSettings textReplacement = new TextReplacementSettings();

    @Comment({"", "Commands settings"})
    private CommandsSettings commands = new CommandsSettings();

    @Comment({"", "DO NOT CHANGE THIS PLEASE"})
    @Setter
    private int migrationVersion = 0;

    // @Comment({"", "Interaction Menu module"})
    // private InteractionMenuSettings interactionMenu = new InteractionMenuSettings();

}
