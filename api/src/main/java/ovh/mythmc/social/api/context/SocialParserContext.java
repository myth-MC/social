package ovh.mythmc.social.api.context;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialParserContext implements SocialContext {
    
    public static SocialParserContextBuilder builder(@NotNull AbstractSocialUser<? extends Object> user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    private final AbstractSocialUser<? extends Object> user;

    private final ChatChannel channel;

    private final Component message;

    private final ChannelType messageChannelType;

    @Experimental
    private final Optional<SocialParserGroup> group;

    SocialParserContext(
        AbstractSocialUser<? extends Object> user,
        ChatChannel channel,
        Component message,
        ChannelType messageChannelType,
        Optional<SocialParserGroup> group) {

        this.user = user;
        this.channel = channel;
        this.message = message;
        this.messageChannelType = messageChannelType;
        this.group = group;
    }

    public static abstract class Builder<T extends Builder<?, ?>, R> {

        protected final AbstractSocialUser<? extends Object> user;

        protected final Component message;

        protected ChatChannel channel;

        protected ChannelType messageChannelType;

        protected Optional<SocialParserGroup> group;

        Builder(AbstractSocialUser<? extends Object> user, Component message) {
            this.user = user;
            this.message = message;
            this.channel = user.mainChannel();
            this.messageChannelType = ChannelType.CHAT;
        }

        public abstract R build();

        public abstract T get();

        public T channel(@NotNull ChatChannel channel) {
            get().channel = channel;
            return get();
        }

        public T messageChannelType(@NotNull ChannelType channelType) {
            get().messageChannelType = channelType;
            return get();
        }

        public T group(@Nullable SocialParserGroup group) {
            get().group = Optional.ofNullable(group);
            return get();
        }

    }

    public static class SocialParserContextBuilder extends Builder<SocialParserContextBuilder, SocialParserContext> {

        SocialParserContextBuilder(AbstractSocialUser<? extends Object> user, Component message) {
            super(user, message);
        }

        @Override
        public SocialParserContextBuilder get() {
            return this;
        }
        
        @Override
        public SocialParserContext build() {
            return new SocialParserContext(
                user, 
                channel, 
                message, 
                messageChannelType,
                group);
        }

    }
    
}
