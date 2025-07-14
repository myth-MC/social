package ovh.mythmc.social.api.chat.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.renderer.feature.ChatRendererFeature;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ChatFormatBuilder {

    public static ChatFormatBuilder empty() {
        return new ChatFormatBuilder();
    }

    public static ChatFormatBuilder from(@NotNull TextComponent base) {
        return new ChatFormatBuilder()
            .append(base);
    }

    private ChatFormatBuilder() { }

    private final List<SocialInjectedValue<?>> injectedValues = new ArrayList<>();

    public ChatFormatBuilder injectValues(@NotNull Iterable<? extends SocialInjectedValue<?>> injectedValues) {
        for (SocialInjectedValue<?> injectedValue : injectedValues) {
            this.injectedValues.add(injectedValue);
        }

        return this;
    }

    public ChatFormatBuilder injectValue(@NotNull SocialInjectedValue<?> injectedValue) {
        this.injectedValues.add(injectedValue);
        return this;
    }

    public ChatFormatBuilder injectValue(@NotNull SocialInjectedValue<?> injectedValue, int index) {
        this.injectedValues.add(index, injectedValue);
        return this;
    }

    public ChatFormatBuilder append(@NotNull TextComponent component, int index) {
        final var injectedValue = SocialInjectedValue.literal(component);
        return injectValue(injectedValue, index);
    }

    public ChatFormatBuilder append(@NotNull TextComponent component) {
        final var injectedValue = SocialInjectedValue.literal(component);
        return injectValue(injectedValue);
    }

    public ChatFormatBuilder appendConditional(@NotNull TextComponent component, @NotNull Predicate<SocialParserContext> predicate, int index) {
        final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component), SocialInjectionParsers.LITERAL, predicate);
        return injectValue(injectedValue, index);
    }

    public ChatFormatBuilder appendConditional(@NotNull TextComponent component, @NotNull Predicate<SocialParserContext> predicate) {
        final var injectedValue = SocialInjectedValue.conditional(SocialInjectedValue.literal(component), SocialInjectionParsers.LITERAL, predicate);
        return injectValue(injectedValue);
    }

    public ChatFormatBuilder appendSpace() {
        return append(Component.space());
    }

    public Component preRenderPrefix(@NotNull AbstractSocialUser target, @NotNull Iterable<? extends ChatRendererFeature> supportedFeatures, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser) {
        // Inject values into context
        parser.injectValues(this.injectedValues);

        Component component = Component.empty();

        for (ChatRendererFeature feature : supportedFeatures) {
            if (feature.isApplicable(message))
                feature.handler().handle(target, this, message, parser);
        }

        for (SocialInjectedValue<?> injectedValue : this.injectedValues) {
            component = injectedValue.parse(parser.withMessage(component));
        }

        return component;
    }

}