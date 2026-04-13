package ovh.mythmc.social.api.context;

import java.util.*;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.text.group.SocialParserGroup;
import ovh.mythmc.social.api.text.injection.value.AbstractSocialInjectedValue;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents the parsing context of a message before it is formatted
 * or dispatched.
 */
public class SocialParserContext implements SocialContext {

    /**
     * Creates a new {@link SocialParserContextBuilder} for the given user
     * and message.
     *
     * @param user    the user sending the message
     * @param message the raw message component
     * @return a new builder instance
     */
    public static @NotNull SocialParserContextBuilder builder(@NotNull SocialUser user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    private final SocialUser user;
    private final ChatChannel channel;
    private final Component message;
    private final ChatChannel.ChannelType messageChannelType;

    @Experimental
    private final SocialParserGroup group;

    @ApiStatus.Experimental
    private final List<SocialInjectedValue<?, ?>> injectedValues;

    protected SocialParserContext(
            @NotNull SocialUser user,
            @NotNull ChatChannel channel,
            @NotNull Component message,
            @NotNull ChatChannel.ChannelType messageChannelType,
            @Nullable SocialParserGroup group,
            @NotNull List<SocialInjectedValue<?, ?>> injectedValues) {

        this.user = user;
        this.channel = channel;
        this.message = message;
        this.messageChannelType = messageChannelType;
        this.group = group;
        this.injectedValues = injectedValues;
    }

    /**
     * Returns the user who sent the message.
     */
    public @NotNull SocialUser user() {
        return user;
    }

    /**
     * Returns the channel associated with this message.
     */
    public @NotNull ChatChannel channel() {
        return channel;
    }

    /**
     * Returns the raw message component.
     */
    public @NotNull Component message() {
        return message;
    }

    /**
     * Returns the channel type.
     */
    public @NotNull ChatChannel.ChannelType messageChannelType() {
        return messageChannelType;
    }

    /**
     * Returns the parser group if present.
     */
    public Optional<SocialParserGroup> group() {
        return Optional.ofNullable(group);
    }

    /**
     * Returns an immutable copy of the injected values.
     */
    public @NotNull List<SocialInjectedValue<?, ?>> injectedValues() {
        return List.copyOf(injectedValues);
    }

    /**
     * Returns all injected values of the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends SocialInjectedValue<?, ?>> List<T> injectedValues(@NotNull Class<T> type) {
        return injectedValues.stream()
                .filter(type::isInstance)
                .map(injectedValue -> (T) injectedValue)
                .toList();
    }

    /**
     * Injects a single value into this context.
     */
    public void injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue) {
        this.injectedValues.add(injectedValue);
    }

    /**
     * Injects multiple values into this context.
     */
    public void injectValues(@NotNull Iterable<? extends SocialInjectedValue<?, ?>> injectedValues) {
        injectedValues.forEach(this.injectedValues::add);
    }

    /**
     * Retrieves an injected value by its identifier.
     */
    public @Nullable <V> V getInjectedValue(@NotNull String identifier) {
        return getInjectedValue(identifier, null);
    }

    /**
     * Retrieves an injected value by its identifier with a default value.
     */
    @SuppressWarnings("unchecked")
    public @Nullable <V> V getInjectedValue(@NotNull String identifier, V defaultValue) {
        return (V) injectedValues(AbstractSocialInjectedValue.Identified.class).stream()
                .filter(injectedValue -> injectedValue.identifier().equals(identifier))
                .map(AbstractSocialInjectedValue::value)
                .findFirst().orElse(defaultValue);
    }

    // --- Fluent "with" methods ---

    public SocialParserContext withUser(@NotNull SocialUser user) {
        return this.user == user ? this : new SocialParserContext(user, channel, message, messageChannelType, group, injectedValues);
    }

    public SocialParserContext withChannel(@NotNull ChatChannel channel) {
        return this.channel == channel ? this : new SocialParserContext(user, channel, message, messageChannelType, group, injectedValues);
    }

    public SocialParserContext withMessage(@NotNull Component message) {
        return this.message == message ? this : new SocialParserContext(user, channel, message, messageChannelType, group, injectedValues);
    }

    public SocialParserContext withMessageChannelType(@NotNull ChatChannel.ChannelType messageChannelType) {
        return this.messageChannelType == messageChannelType ? this : new SocialParserContext(user, channel, message, messageChannelType, group, injectedValues);
    }

    public SocialParserContext withGroup(@Nullable SocialParserGroup group) {
        return this.group == group ? this : new SocialParserContext(user, channel, message, messageChannelType, group, injectedValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialParserContext that)) return false;
        return Objects.equals(user, that.user) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(message, that.message) &&
                messageChannelType == that.messageChannelType &&
                Objects.equals(group, that.group) &&
                Objects.equals(injectedValues, that.injectedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, channel, message, messageChannelType, group, injectedValues);
    }

    @Override
    public String toString() {
        return "SocialParserContext{" +
                "user=" + user +
                ", channel=" + channel +
                ", message=" + message +
                ", messageChannelType=" + messageChannelType +
                ", group=" + group +
                ", injectedValues=" + injectedValues +
                '}';
    }

    /**
     * Base builder implementation for {@link SocialParserContext}.
     */
    public static abstract class Builder<T extends Builder<?, ?>, R> {

        protected final SocialUser user;
        protected final Component message;
        protected ChatChannel channel;
        protected ChatChannel.ChannelType messageChannelType;
        protected SocialParserGroup group;
        protected List<SocialInjectedValue<?, ?>> injectedValues;

        Builder(SocialUser user, Component message) {
            this.user = user;
            this.message = message;
            this.channel = user.mainChannel().get();
            this.messageChannelType = ChatChannel.ChannelType.CHAT;
            this.injectedValues = new ArrayList<>();
        }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public T channel(@NotNull ChatChannel channel) {
            this.channel = channel;
            return self();
        }

        public T messageChannelType(@NotNull ChatChannel.ChannelType channelType) {
            this.messageChannelType = channelType;
            return self();
        }
    
        public T group(@Nullable SocialParserGroup group) {
            this.group = group;
            return self();
        }

        public T injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue) {
            this.injectedValues.add(injectedValue);
            return self();
        }
    
        public T injectValues(@NotNull Iterable<? extends SocialInjectedValue<?, ?>> injectedValues) {
            injectedValues.forEach(this.injectedValues::add);
            return self();
        }
    }

    /**
     * Default builder implementation for {@link SocialParserContext}.
     */
    public static class SocialParserContextBuilder extends Builder<SocialParserContextBuilder, SocialParserContext> {

        SocialParserContextBuilder(SocialUser user, Component message) {
            super(user, message);
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

