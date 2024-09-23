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
    private ChatConfig chat = new ChatConfig();

    @Comment({"", "System messages module"})
    private SystemMessagesConfig systemMessages = new SystemMessagesConfig();

    @Comment({"", "Commands settings"})
    private CommandsConfig commands = new CommandsConfig();

    @Comment({"", "Announcements module"})
    private AnnouncementsConfig announcements = new AnnouncementsConfig();

    @Comment({"", "Reactions module"})
    private ReactionsConfig reactions = new ReactionsConfig();

    @Comment({"", "Interaction Menu module"})
    private InteractionMenuConfig interactionMenu = new InteractionMenuConfig();

}
