package ovh.mythmc.social.api.context;

import java.util.*;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.AbstractSocialInjectedValue;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Getter
@Accessors(fluent = true)
@With
@EqualsAndHashCode
@ToString
public class SocialParserContext implements SocialContext {
    
    public static SocialParserContextBuilder builder(@NotNull AbstractSocialUser user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    private final AbstractSocialUser user;

    private final ChatChannel channel;

    private final Component message;

    private final ChatChannel.ChannelType messageChannelType;

    @Experimental
    private final SocialParserGroup group;

    @ApiStatus.Experimental
    @With(AccessLevel.NONE)
    private final List<SocialInjectedValue<?>> injectedValues;

    SocialParserContext(
        AbstractSocialUser user,
        ChatChannel channel,
        Component message,
        ChatChannel.ChannelType messageChannelType,
        SocialParserGroup group,
        List<SocialInjectedValue<?>> injectedValues) {

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

    public List<SocialInjectedValue<?>> injectedValues() {
        return List.copyOf(injectedValues);
    }

    @SuppressWarnings("unchecked")
    public <T extends SocialInjectedValue<?>> List<T> injectedValues(@NotNull Class<T> type) {
        return injectedValues().stream()
            .filter(type::isInstance)
            .map(injectedValue -> (T) injectedValue)
            .toList();
    }

    public void injectValue(@NotNull SocialInjectedValue<?> injectedValue) {
        this.injectedValues.add(injectedValue);
    }

    public void injectValues(@NotNull Iterable<? extends SocialInjectedValue<?>> injectedValues) {
        injectedValues.forEach(this.injectedValues::add);
    }

    public @Nullable <V> V getInjectedValue(@NotNull String identifier) {
        return getInjectedValue(identifier, null);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <V> V getInjectedValue(@NotNull String identifier, V defaultValue) {
        return (V) injectedValues(AbstractSocialInjectedValue.Identified.class).stream()
            .filter(injectedValue -> injectedValue.identifier().equals(identifier))
            .map(AbstractSocialInjectedValue::value)
            .findFirst().orElse(defaultValue);
    }

    public static abstract class Builder<T extends Builder<?, ?>, R> {

        protected final AbstractSocialUser user;

        protected final Component message;

        protected ChatChannel channel;

        protected ChatChannel.ChannelType messageChannelType;

        protected SocialParserGroup group;

        protected List<SocialInjectedValue<?>> injectedValues;

        Builder(AbstractSocialUser user, Component message) {
            this.user = user;
            this.message = message;
            this.channel = user.mainChannel();
            this.messageChannelType = ChatChannel.ChannelType.CHAT;
            this.injectedValues = new ArrayList<>();
        }

        public abstract R build();

        public abstract T get();

        public T channel(@NotNull ChatChannel channel) {
            get().channel = channel;
            return get();
        }

        public T messageChannelType(@NotNull ChatChannel.ChannelType channelType) {
            get().messageChannelType = channelType;
            return get();
        }

        public T group(@Nullable SocialParserGroup group) {
            get().group = group;
            return get();
        }

        public T injectValue(@NotNull SocialInjectedValue<?> injectedValue) {
            get().injectedValues.add(injectedValue);
            return get();
        }

        public T injectValues(@NotNull Iterable<? extends SocialInjectedValue<?>> injectedValues) {
            injectedValues.forEach(get().injectedValues::add);
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
    
}
