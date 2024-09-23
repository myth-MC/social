package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.*;

@Configuration
@Getter
public class SocialSettings {

    @Comment("Enabling this will send more logs to console to help debugging")
    private boolean debug = false;

    @Comment({"", "Chat module"})
    private ChatConfig chat = new ChatConfig();

    @Comment({"", "SystemMessages module"})
    private SystemMessagesConfig systemMessages = new SystemMessagesConfig();

    @Comment({"", "Announcements module"})
    private AnnouncementsConfig announcements = new AnnouncementsConfig();

    @Comment({"", "Reactions module"})
    private ReactionsConfig reactions = new ReactionsConfig();

    @Comment({"", "Interaction Menu module"})
    private InteractionMenuConfig interactionMenu = new InteractionMenuConfig();

}
