package ovh.mythmc.social.api.chat.renderer.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.format.ChatFormatBuilder;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

import java.util.function.Predicate;


/**
 * Represents a feature that can be applied when rendering chat messages.
 * <p>
 * A {@code ChatRendererFeature} encapsulates:
 * <ul>
 *     <li>a {@link Handler} that modifies the message format before rendering,</li>
 *     <li>a {@link Decorator} that can modify the {@link Component} visually,</li>
 *     <li>a {@link Predicate} condition that determines whether this feature applies to a given message context.</li>
 * </ul>
 * 
 * <p>
 * Features can be created using the static {@link #builder(Handler)} method or with specialized helper methods
 * such as {@link #replies(int)} and {@link #companion()}. Builders allow additional customization
 * of the condition and decorator before building the final feature.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * ChatRendererFeature repliesFeature = ChatRendererFeature.replies(0);
 * ChatRendererFeature companionFeature = ChatRendererFeature.companion();
 * }</pre>
 *
 * @see #builder(Handler)
 * @see #replies(int)
 * @see #companion()
 */
public class ChatRendererFeature {

    /**
     * Creates a builder for a custom chat feature.
     *
     * @param handler the handler to modify the message format
     * @return a {@link ChatFeatureBuilder} for configuring decorators and conditions
     */
    public static ChatFeatureBuilder builder(@NotNull Handler handler) {
        return new ChatFeatureBuilder(handler);
    }

    /**
     * Returns a feature that renders reply icons for messages that are replies.
     *
     * @param injectionIndex the index in the format to inject the reply icon
     * @return a {@link ChatRendererFeature} that applies reply icons
     */
    public static ChatRendererFeature replies(int injectionIndex) {
        return ChatRendererFeature.builder((target, format, message, parser) -> {
            if (message.isReply())
                format.injectValue(SocialInjectedValue.literal(SocialChatRendererUtil.getReplyIcon(target, message)),
                        injectionIndex);
        })
                .decorator((context, component) -> {
                    final int idToReply = context.isReply() ? context.replyId() : context.id();
                    return component.applyFallbackStyle(ClickEvent.suggestCommand("(re:#" + idToReply + ") "));
                })
                .build();
    }

    /**
     * Returns a feature that appends companion-related components to messages, if the sender has a companion.
     *
     * @return a {@link ChatRendererFeature} for rendering companions
     */
    public static ChatRendererFeature companion() {
        return ChatRendererFeature.builder((target, format, message, parser) -> {
            if (target.companion().isPresent())
                format.append(CompanionModUtils.asChannelable(Component.empty(), message.channel()), 0);
        })
                .build();
    }

    private final Handler handler;

    private final Decorator decorator;

    private final Predicate<SocialRegisteredMessageContext> condition;

    private ChatRendererFeature(@NotNull Handler handler, @NotNull Decorator decorator,
            @NotNull Predicate<SocialRegisteredMessageContext> condition) {
        this.handler = handler;
        this.decorator = decorator;
        this.condition = condition;
    }

    /**
     * Returns the {@link Handler} of this feature.
     */
    public Handler handler() {
        return this.handler;
    }

    /**
     * Returns the {@link Decorator} of this feature.
     */
    public Decorator decorator() {
        return this.decorator;
    }

    /**
     * Tests whether this feature is applicable to a given message context.
     *
     * @param context the {@link SocialRegisteredMessageContext} to test
     * @return true if this feature applies, false otherwise
     */
    public boolean isApplicable(@NotNull SocialRegisteredMessageContext context) {
        return condition.test(context);
    }

    /**
     * Builder for {@link ChatRendererFeature}.
     *
     * @param <T> the concrete builder type
     * @param <R> the feature type being built
     */
    public abstract static class Builder<T extends Builder<T, R>, R extends ChatRendererFeature> {

        protected final Handler handler;

        protected Predicate<SocialRegisteredMessageContext> condition;

        protected Decorator decorator;

        protected Builder(@NotNull Handler handler) {
            this.handler = handler;
            this.condition = context -> true;
            this.decorator = (context, component) -> component;
        }

        protected abstract T get();

        /**
         * Sets the condition under which this feature applies.
         *
         * @param condition a predicate evaluating a message context
         * @return the builder for chaining
         */
        public T condition(@NotNull Predicate<SocialRegisteredMessageContext> condition) {
            get().condition = condition;
            return get();
        }

        /**
         * Sets the decorator to modify the rendered {@link Component}.
         *
         * @param decorator the decorator function
         * @return the builder for chaining
         */
        public T decorator(@NotNull Decorator decorator) {
            get().decorator = decorator;
            return get();
        }

        /**
         * Builds the {@link ChatRendererFeature}.
         */
        public abstract R build();

    }

    /**
     * Builder implementation for simple chat features.
     */
    public static final class ChatFeatureBuilder extends Builder<ChatFeatureBuilder, ChatRendererFeature> {

        private ChatFeatureBuilder(@NotNull Handler handler) {
            super(handler);
        }

        @Override
        protected ChatFeatureBuilder get() {
            return this;
        }

        @Override
        public ChatRendererFeature build() {
            return new ChatRendererFeature(handler, decorator, condition);
        }

    }

    /**
     * Functional interface representing a handler that modifies a message's format before rendering.
     */
    @FunctionalInterface
    public interface Handler {

        void handle(@NotNull SocialUser target, @NotNull ChatFormatBuilder format,
                @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser);

    }

    /**
     * Functional interface representing a decorator that can visually modify a message's {@link Component}.
     */
    @FunctionalInterface
    public interface Decorator {

        @NotNull
        Component decorate(@NotNull SocialRegisteredMessageContext context, @NotNull Component component);

    }

}
