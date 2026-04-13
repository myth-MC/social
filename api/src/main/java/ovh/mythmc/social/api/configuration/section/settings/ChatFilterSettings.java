package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Settings for the chat filter module.
 */
@Configuration
public class ChatFilterSettings {

    @Comment("Whether the filter module should be enabled")
    private boolean enabled = true;

    @Comment("Whether the built-in IP filter should be enabled")
    private boolean ipFilter = true;

    @Comment("Whether the built-in URL filter should be enabled")
    private boolean urlFilter = true;

    @Comment("Whether the built-in flood/spam filter should be enabled")
    private boolean floodFilter = true;

    @Comment("Time in milliseconds that a player has to wait before sending another message")
    private int floodFilterCooldownInMilliseconds = 1000;

    @Comment("Words or sentences in this section will also be filtered")
    private List<String> literalFilter = List.of("example sentence that should not be said");

    @Comment("You can also create your own custom regex filters")
    private List<String> customRegexFilter = List.of();

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isIpFilter() {
        return ipFilter;
    }

    public boolean isUrlFilter() {
        return urlFilter;
    }

    public boolean isFloodFilter() {
        return floodFilter;
    }

    public int getFloodFilterCooldownInMilliseconds() {
        return floodFilterCooldownInMilliseconds;
    }

    public @NotNull List<String> getLiteralFilter() {
        return literalFilter;
    }

    public @NotNull List<String> getCustomRegexFilter() {
        return customRegexFilter;
    }

}

