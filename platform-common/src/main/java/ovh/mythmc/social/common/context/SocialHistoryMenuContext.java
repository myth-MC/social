package ovh.mythmc.social.common.context;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;

/**
 * Context for a social history menu.
 */
public class SocialHistoryMenuContext extends SocialMenuContext {

    private final List<SocialRegisteredMessageContext> messages;

    private final HeaderType headerType;

    private final Order order;

    private final ChatChannel channel;

    private final int replyId;

    protected SocialHistoryMenuContext(Builder<?> builder) {
        super(builder);
        this.messages = builder.messages;
        this.headerType = builder.headerType;
        this.order = builder.order;
        this.channel = builder.channel;
        this.replyId = builder.replyId;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    public @NotNull List<SocialRegisteredMessageContext> messages() {
        return messages;
    }

    public @NotNull HeaderType headerType() {
        return headerType;
    }

    public @NotNull Order order() {
        return order;
    }

    public @Nullable ChatChannel channel() {
        return channel;
    }

    public int replyId() {
        return replyId;
    }

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

    public static class Builder<B extends Builder<B>> extends SocialMenuContext.Builder<B> {
        protected List<SocialRegisteredMessageContext> messages;
        protected HeaderType headerType = HeaderType.GLOBAL;
        protected Order order = Order.MOST_RECENT_TO_OLDEST;
        protected ChatChannel channel = null;
        protected int replyId = 0;

        protected Builder() {}

        public B messages(List<SocialRegisteredMessageContext> messages) {
            this.messages = messages;
            return self();
        }

        public B headerType(HeaderType headerType) {
            this.headerType = headerType;
            return self();
        }

        public B order(Order order) {
            this.order = order;
            return self();
        }

        public B channel(ChatChannel channel) {
            this.channel = channel;
            return self();
        }

        public B replyId(int replyId) {
            this.replyId = replyId;
            return self();
        }

        @Override
        public SocialHistoryMenuContext build() {
            return new SocialHistoryMenuContext(this);
        }
    }

}

