package ovh.mythmc.social.common.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ovh.mythmc.social.api.chat.ChatChannel;
import lombok.Setter;

@Getter
@SuperBuilder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialHistoryMenuContext extends SocialMenuContext {

    @Builder.Default
    private final Scope scope = Scope.GLOBAL;

    @Builder.Default
    private final Order order = Order.MOST_RECENT_TO_OLDEST;

    @Builder.Default
    private final ChatChannel channel = null;

    @Builder.Default
    private final PlayerVisibility playerVisibility = PlayerVisibility.ALL;

    public enum Scope {
        GLOBAL,
        PLAYER
    }

    public enum Order {
        OLDEST_TO_MOST_RECENT,
        MOST_RECENT_TO_OLDEST
    }

    public enum PlayerVisibility {
        ALL,
        ONLINE,
        OFFLINE
    }
    
}
