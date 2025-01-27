package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.settings.*;

@Configuration
@Getter
public final class LegacySocialSettings implements SocialSettings {

    public boolean nagAdmins = true;

    @Comment({"", "General settings"})
    private GeneralSettings general = new GeneralSettings();

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

    @Comment({"", "Server links module"})
    private ServerLinksSettings serverLinks = new ServerLinksSettings();

    @Comment({"", "Text replacement module"})
    private TextReplacementSettings textReplacement = new TextReplacementSettings();

    @Comment({"", "Commands settings"})
    private CommandsSettings commands = new CommandsSettings();

}
