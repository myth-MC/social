package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.AnnouncementsConfig;
import ovh.mythmc.social.api.configuration.sections.ChatConfig;
import ovh.mythmc.social.api.configuration.sections.InteractionMenuConfig;
import ovh.mythmc.social.api.configuration.sections.ReactionsConfig;

@Configuration
@Getter
public class SocialSettings {

    @Comment("Enabling this will send more logs to console to help debugging")
    private boolean debug = false;

    @Comment({"", "Chat module"})
    private ChatConfig chat = new ChatConfig();

    @Comment({"", "Announcements module"})
    private AnnouncementsConfig announcements = new AnnouncementsConfig();

    @Comment({"", "Reactions module"})
    private ReactionsConfig reactions = new ReactionsConfig();

    @Comment({"", "Interaction Menu module"})
    private InteractionMenuConfig interactionMenu = new InteractionMenuConfig();

}
