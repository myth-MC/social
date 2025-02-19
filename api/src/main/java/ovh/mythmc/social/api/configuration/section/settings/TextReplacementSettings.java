package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class TextReplacementSettings {

    @Comment({"Whether the text replacement module should be enabled", "This allows filters, emojis and keywords to be used in signs, books, anvils..."})
    private boolean enabled = true;

    @Comment("Whether to replace text in anvils or not")
    private boolean anvil = true;

    @Comment("Whether to replace text in books or not")
    private boolean books = true;

    @Comment("Whether to replace text in signs or not")
    private boolean signs = true;

}
