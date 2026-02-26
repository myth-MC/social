package ovh.mythmc.social.api.chat.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Builds the prefix component for a
 * {@link ovh.mythmc.social.api.chat.channel.ChatChannel}.
 *
 * <p>
 * A format is assembled by appending {@link SocialInjectedValue} slots in
 * order.
 * Static text components are wrapped with {@link #append(TextComponent)}, while
 * context-dependent elements can use
 * {@link #appendConditional(TextComponent, java.util.function.Predicate)}
 * or arbitrary {@link SocialInjectedValue} instances via
 * {@link #injectValue(SocialInjectedValue)}.
 */
public final class ChatFormatBuilder {

    /**
     * Creates an empty format with no injected values.
     *
     * @return an empty builder
     */
    public static ChatFormatBuilder empty() {
        return new ChatFormatBuilder();
    }

    /**
     * Creates a format pre-populated with the given text component.
     *
     * @param base the initial component
     * @return a builder with the component appended
     */
    public static ChatFormatBuilder from(@NotNull TextComponent base) {
        return new ChatFormatBuilder()
                .append(base);
    }

    private ChatFormatBuilder() {
    }

    private final List<SocialInjectedValue<?, ?>> injectedValues = new ArrayList<>();

    /**
     * Appends each of the given injected values to this format.
     *
     * @param injectedValues the values to append
     * @return this builder
     */
    public ChatFormatBuilder injectValues(@NotNull Iterable<? extends SocialInjectedValue<?, ?>> injectedValues) {
        for (SocialInjectedValue<?, ?> injectedValue : injectedValues) {
            this.injectedValues.add(injectedValue);
        }

        return this;
    }

    /**
     * Appends a single injected value to the end of this format.
     *
     * @param injectedValue the value to append
     * @return this builder
     */
    public ChatFormatBuilder injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue) {
        this.injectedValues.add(injectedValue);
        return this;
    }

    /**
     * Inserts a single injected value at the given index.
     *
     * @param injectedValue the value to insert
     * @param index         the position to insert at
     * @return this builder
     */
    public ChatFormatBuilder injectValue(@NotNull SocialInjectedValue<?, ?> injectedValue, int index) {
        this.injectedValues.add(index, injectedValue);
        return this;
    }

    /**
     * Inserts a static text component as a literal injected value at the given
     * index.
     *
     * @param component the component to insert
     * @param index     the position to insert at
     * @return this builder
     */
    public ChatFormatBuilder append(@NotNull TextComponent component, int index) {
        final var injectedValue = SocialInjectedValue.literal(component);
        return injectValue(injectedValue, index);
    }

    /**
     * Appends a static text component as a literal injected value.
     *
     * @param component the component to append
     * @return this builder
     */
    public ChatFormatBuilder append(@NotNull TextComponent component) {
        final var injectedValue = SocialInjectedValue.literal(component);
        return injectValue(injectedValue);
    }

    /**
     * Inserts a conditional text component at the given index; it is only rendered
     * when {@code predicate} evaluates to {@code true} for the current parser
     * context.
     *
     * @param component the component to show when the predicate matches
     * @param predicate the condition under which the component is rendered
     * @param index     the position to insert at
     * @return this builder
     */
    public ChatFormatBuilder appendConditional(@NotNull TextComponent component,
            @NotNull Predicate<SocialParserContext> predicate, int index) {
        final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component),
                SocialInjectionParsers.LITERAL, predicate);
        return injectValue(injectedValue, index);
    }

    /**
     * Appends a conditional text component; it is only rendered when
     * {@code predicate}
     * evaluates to {@code true} for the current parser context.
     *
     * @param component the component to show when the predicate matches
     * @param predicate the condition under which the component is rendered
     * @return this builder
     */
    public ChatFormatBuilder appendConditional(@NotNull TextComponent component,
            @NotNull Predicate<SocialParserContext> predicate) {
        final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component),
                SocialInjectionParsers.LITERAL, predicate);
        return injectValue(injectedValue);
    }

    /**
     * Appends a single space as a literal injected value.
     *
     * @return this builder
     */
    public ChatFormatBuilder appendSpace() {
        return append(Component.space());
    }

    /**
     * Evaluates all renderer features and then renders the full prefix component.
     *
     * <p>
     * This is called internally by {@link ovh.mythmc.social.api.chat.channel.ChatChannel#prefix(SocialUser, SocialRegisteredMessageContext, SocialParserContext)} implementations and
     * should
     * not normally be invoked directly from plugin code.
     *
     * @param target            the player the prefix is being rendered for
     * @param supportedFeatures the renderer features active on the channel
     * @param message           the registered message context
     * @param parser            the parser context, which receives the injected
     *                          values
     * @return the fully resolved prefix component
     */
    public Component preRenderPrefix(@NotNull SocialUser target,
            @NotNull Iterable<? extends ChatRendererFeature> supportedFeatures,
            @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser) {
        // Inject values into context
        parser.injectValues(this.injectedValues);

        Component component = Component.empty();

        for (ChatRendererFeature feature : supportedFeatures) {
            if (feature.isApplicable(message))
                feature.handler().handle(target, this, message, parser);
        }

        for (SocialInjectedValue<?, ?> injectedValue : this.injectedValues) {
            component = injectedValue.parse(parser.withMessage(component));
        }

        return component;
    }

}