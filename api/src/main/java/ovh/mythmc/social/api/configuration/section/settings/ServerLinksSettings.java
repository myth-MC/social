package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class ServerLinksSettings {

    @Comment({"Whether custom server links should be enabled", "Server links are a feature introduced in snapshot 24w21a that adds custom links to the pause menu"})
    private boolean enabled = true;

    @Comment({"Use 'displayName' if you want the button to have custom text with colors, emojis...", "Use 'type' if you want the button to be translated depending on player's language", "List of types: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ServerLinks.Type.html"})
    private List<ServerLink> links = List.of(
            new ServerLink("<blue>:raw_comet:</blue> Website", null,"https://example.com"),
            new ServerLink("<yellow>:raw_heart:</yellow> Donations", null, "https://example.com"),
            new ServerLink(null, "COMMUNITY_GUIDELINES", "https://example.com"),
            new ServerLink(null, "COMMUNITY", "https://discord.com"),
            new ServerLink(null, "SUPPORT", "https://example.com")
    );

    public record ServerLink(String displayName,
                             String type,
                             String url) { }

}
