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
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents the parsing context of a message before it is formatted
 * or dispatched.
 *
 * <p>
 * A {@code SocialParserContext} provides all necessary information required
 * during the parsing phase of a message, including:
 * </p>
 *
 * <ul>
 *     <li>The {@link SocialUser} who sent the message</li>
 *     <li>The {@link ChatChannel} the message is associated with</li>
 *     <li>The raw {@link Component} message content</li>
 *     <li>The {@link ChatChannel.ChannelType} of the message</li>
 *     <li>An optional {@link SocialParserGroup} defining parser behavior</li>
 *     <li>A collection of {@link SocialInjectedValue injected values}</li>
 * </ul>
 *
 * @see SocialContext
 * @see SocialInjectedValue
 * @see SocialParserGroup
 */
@Getter
@Accessors(fluent = true)
@With
@EqualsAndHashCode
@ToString
public class SocialParserContext implements SocialContext {

    /**
     * Creates a new {@link SocialParserContextBuilder} for the given user
     * and message.
     *
     * @param user    the user sending the message
     * @param message the raw message component
     * @return a new builder instance
     */
    public static SocialParserContextBuilder builder(@NotNull SocialUser user, @NotNull Component message) {
        return new SocialParserContextBuilder(user, message);
    }

    /**
     * The user who sent the message.
     */
    private final SocialUser user;

    /**
     * The channel associated with this message.
     */
    private final ChatChannel channel;

    /**
     * The raw message component prior to formatting or transformation.
     */
    private final Component message;

    /**
     * The type of channel this message is being processed as.
     */
    private final ChatChannel.ChannelType messageChannelType;

    /**
     * The parser group controlling how this message should be parsed.
     */
    @Experimental
    private final SocialParserGroup group;

    /**
     * A mutable collection of injected values associated with this parsing context.
     */
    @ApiStatus.Experimental
    @With(AccessLevel.NONE)
    private final List<SocialInjectedValue<?, ?>> injectedValues;

    SocialParserContext(
            SocialUser user,
            ChatChannel channel,
            Component message,
            ChatChannel.ChannelType messageChannelType,
            SocialParserGroup group,
            List<SocialInjectedValue<?, ?>> injectedValues) {

        this.user = user;
        this.channel = channel;
        this.message = message;
        this.messageChannelType = messageChannelType;
        this.group = group;
        this.injectedValues = injectedValues;
    }

    /**
     * Returns the parser group if present.
     *
     * @return an {@link Optional} containing the {@link SocialParserGroup},
     *         or empty if none is set
     */
    public Optional<SocialParserGroup> group() {
        return Optional.ofNullable(group);
    }

    /**
     * Returns an immutable copy of the injected values.
     *
     * @return an unmodifiable list of injected values
     */
    public List<SocialInjectedValue<?, ?>> injectedValues() {
        return List.copyOf(injectedValues);
    }

    /**
     * Returns all injected values of the specified type.
     *
     * @param type the injected value type
     * @param <T>  the injected value subtype
     * @return a list of injected values matching the given type
     */
    @SuppressWarnings("unchecked")
    public <T extends SocialInjectedValue<?, ?>> List<T> injectedValues(@NotNull Class<T> type) {
        return injectedValues().stream()
                .filter(type::isInstance)
                .map(injectedValue -> (T) injectedValue)
                .toList();
    }

    /**
     * Injects a single value into this context.
     *
     * @param injectedValue the value to inject
     */
    public void injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue) {
        this.injectedValues.add(injectedValue);
    }

    /**
     * Injects multiple values into this context.
     *
     * @param injectedValues the values to inject
     */
    public void injectValues(@NotNull Iterable<? extends SocialInjectedValue<?, ?>> injectedValues) {
        injectedValues.forEach(this.injectedValues::add);
    }

    /**
     * Retrieves an injected value by its identifier.
     *
     * @param identifier the unique identifier
     * @param <V>        the value type
     * @return the injected value, or {@code null} if not present
     */
    public @Nullable <V> V getInjectedValue(@NotNull String identifier) {
        return getInjectedValue(identifier, null);
    }

    /**
     * Retrieves an injected value by its identifier, returning a default
     * value if none is found.
     *
     * @param identifier   the unique identifier
     * @param defaultValue the value to return if no match is found
     * @param <V>          the value type
     * @return the injected value or {@code defaultValue} if not present
     */
    @SuppressWarnings("unchecked")
    public @Nullable <V> V getInjectedValue(@NotNull String identifier, V defaultValue) {
        return (V) injectedValues(AbstractSocialInjectedValue.Identified.class).stream()
                .filter(injectedValue -> injectedValue.identifier().equals(identifier))
                .map(AbstractSocialInjectedValue::value)
                .findFirst().orElse(defaultValue);
    }

    /**
     * Base builder implementation for {@link SocialParserContext}.
     *
     * @param <T> the concrete builder type
     * @param <R> the build result type
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


        /**
         * Builds the {@link SocialParserContext}.
         *
         * @return the constructed context
         */
        public abstract R build();

        /**
         * Returns the current builder instance.
         *
         * @return this builder
         */
        public abstract T get();

        /**
         * Sets the channel of the context.
         *
         * @param channel the chat channel
         * @return this builder
         */
        public T channel(@NotNull ChatChannel channel) {
            get().channel = channel;
            return get();
        }

        /**
         * Sets the message channel type.
         *
         * @param channelType the channel type
         * @return this builder
         */
        public T messageChannelType(@NotNull ChatChannel.ChannelType channelType) {
            get().messageChannelType = channelType;
            return get();
        }
    
        /**
         * Sets the parser group.
         *
         * @param group the parser group
         * @return this builder
         */
        public T group(@Nullable SocialParserGroup group) {
            get().group = group;
            return get();
        }

        /**
         * Injects a value into the builder.
         *
         * @param injectedValue the value to inject
         * @return this builder
         */
        public T injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue) {
            get().injectedValues.add(injectedValue);
            return get();
        }
    
        /**
         * Injects multiple values into the builder.
         *
         * @param injectedValues the values to inject
         * @return this builder
         */
        public T injectValues(@NotNull Iterable<? extends SocialInjectedValue<?, ?>> injectedValues) {
            injectedValues.forEach(get().injectedValues::add);
            return get();
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
        public SocialParserContextBuilder get() {
            return this;
        }

        /**
         * Builds a new {@link SocialParserContext} instance.
         *
         * @return the constructed context
         */
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
