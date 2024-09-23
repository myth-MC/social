package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.settings.*;

@Configuration
@Getter
public class SocialSettings {

    @Comment("Enabling this will send more logs to console to help debugging")
    private boolean debug = true;

    @Comment({"", "Chat module"})
    private ChatSettings chat = new ChatSettings();

    @Comment({"", "System messages module"})
    private SystemMessagesSettings systemMessages = new SystemMessagesSettings();

    @Comment({"", "Commands settings"})
    private CommandsSettings commands = new CommandsSettings();

    @Comment({"", "Announcements module"})
    private AnnouncementsSettings announcements = new AnnouncementsSettings();

    @Comment({"", "Reactions module"})
    private ReactionsSettings reactions = new ReactionsSettings();

    @Comment({"", "Interaction Menu module"})
    private InteractionMenuSettings interactionMenu = new InteractionMenuSettings();

}
