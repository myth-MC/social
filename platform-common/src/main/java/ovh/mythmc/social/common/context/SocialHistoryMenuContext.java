package ovh.mythmc.social.common.context;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import lombok.Setter;

@Getter
@SuperBuilder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialHistoryMenuContext extends SocialMenuContext {

    private final List<SocialRegisteredMessageContext> messages;

    @Builder.Default
    private final HeaderType headerType = HeaderType.GLOBAL;

    @Builder.Default
    private final Order order = Order.MOST_RECENT_TO_OLDEST;

    @Builder.Default
    private final ChatChannel channel = null;

    @Builder.Default
    private final int replyId = 0;

    public enum HeaderType {
        GLOBAL,
        CHANNEL,
        PLAYER,
        THREAD
    }

    public enum Order {
        OLDEST_TO_MOST_RECENT,
        MOST_RECENT_TO_OLDEST
    }
    
}
