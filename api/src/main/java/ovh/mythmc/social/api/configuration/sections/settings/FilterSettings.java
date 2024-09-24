package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Configuration
@Getter
public class FilterSettings {

    @Comment("Whether the filter module should be enabled or disabled")
    private boolean enabled = true;

    @Comment("Whether the built-in IP filter should be enabled or disabled")
    private boolean ipFilter = true;

    @Comment("Whether the built-in URL filter should be enabled or disabled")
    private boolean urlFilter = true;

    @Comment("Whether the built-in flood/spam filter should be enabled or disabled")
    private boolean floodFilter = true;

    @Comment("Time in milliseconds that a player has to wait before sending another message")
    private int floodFilterCooldownInMilliseconds = 1000;

    @Comment("Words or sentences in this section will also be filtered")
    private List<String> literalFilter = List.of("example sentence that should not be said");

    @Comment("You can also create your own custom regex filters")
    private List<String> customRegexFilter = List.of();

}
