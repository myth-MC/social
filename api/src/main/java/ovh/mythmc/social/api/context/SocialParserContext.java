package ovh.mythmc.social.api.context;

import java.util.*;

import lombok.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialParserContext implements SocialContext {
    
    public static SocialParserContextBuilder builder(@NotNull AbstractSocialUser user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    private final AbstractSocialUser user;

    private final ChatChannel channel;

    private final Component message;

    private final ChannelType messageChannelType;

    @Experimental
    private final SocialParserGroup group;

    @ApiStatus.Experimental
    @With(AccessLevel.NONE)
    private final List<InjectedValue> injectedValues;

    SocialParserContext(
        AbstractSocialUser user,
        ChatChannel channel,
        Component message,
        ChannelType messageChannelType,
        SocialParserGroup group,
        List<InjectedValue> injectedValues) {

        this.user = user;
        this.channel = channel;
        this.message = message;
        this.messageChannelType = messageChannelType;
        this.group = group;
        this.injectedValues = injectedValues;
    }

    public Optional<SocialParserGroup> group() {
        return Optional.ofNullable(group);
    }

    public List<InjectedValue> injectedValues() {
        return List.copyOf(injectedValues);
    }

    @SuppressWarnings("unchecked")
    public <V> Optional<V> getInjectedValue(@NotNull String identifier) {
        return (Optional<V>) this.injectedValues.stream()
            .filter(injectedValue -> injectedValue.identifier().equals(identifier))
            .findFirst();
    }

    public static abstract class Builder<T extends Builder<?, ?>, R> {

        protected final AbstractSocialUser user;

        protected final Component message;

        protected ChatChannel channel;

        protected ChannelType messageChannelType;

        protected SocialParserGroup group;

        protected List<InjectedValue> injectedValues;

        Builder(AbstractSocialUser user, Component message) {
            this.user = user;
            this.message = message;
            this.channel = user.mainChannel();
            this.messageChannelType = ChannelType.CHAT;
            this.injectedValues = new ArrayList<>();
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
            get().group = group;
            return get();
        }

        public T injectValue(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionParser parser) {
            get().injectedValues.add(new InjectedValue(identifier, value, parser));
            return get();
        }

    }

    public static class SocialParserContextBuilder extends Builder<SocialParserContextBuilder, SocialParserContext> {

        SocialParserContextBuilder(AbstractSocialUser user, Component message) {
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
                group,
                injectedValues);
        }

    }

    public static class InjectedValue {

        private final String identifier;

        private final Object value;

        private final SocialInjectionParser parser;

        private InjectedValue(String identifier, Object value, SocialInjectionParser parser) {
            this.identifier = identifier;
            this.value = value;
            this.parser = parser;
        }

        public String identifier() {
            return identifier;
        }

        public Object value() {
            return value;
        }

        public SocialInjectionParser parser() {
            return parser;
        }

    }
    
}
