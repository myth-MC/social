package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class PacketsSettings {

    @Comment({"Whether packet-based features should be enabled", "Requires PacketEvents to work: https://github.com/retrooper/packetevents/"})
    private boolean enabled = true;

    @Comment("Emoji tab completion in chat")
    private boolean chatEmojiTabCompletion = true;

    @Comment("Keyword tab completion in chat")
    private boolean chatKeywordTabCompletion = true;

    @Comment("Server links module")
    private ServerLinksSettings serverLinks = new ServerLinksSettings();

}
