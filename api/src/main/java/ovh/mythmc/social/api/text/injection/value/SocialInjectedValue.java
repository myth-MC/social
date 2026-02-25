package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.text.injection.conditional.SocialInjectedConditionalValue;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;

import java.util.function.Predicate;

/**
 * A typed value that can be injected into a chat format and parsed into a {@link Component}.
 *
 * <p>The {@code T} parameter is the raw value type (e.g. {@link TextComponent}, {@link TagResolver}).
 * The {@code Self} parameter is the concrete subtype, used so that {@link #parser()} can return a
 * {@link SocialInjectionParser} typed to the exact subclass without any unchecked cast.</p>
 *
 * @param <T>    the type of the wrapped value
 * @param <Self> the concrete implementing subtype (F-bounded self-type)
 */
public interface SocialInjectedValue<T, Self extends SocialInjectedValue<T, Self>> {

    static <T extends SocialInjectedValue<?, T>, P extends SocialInjectionParser<T>> SocialInjectedConditionalValue<T, P> conditional(@NotNull T value, @NotNull P conditionalParser, @NotNull Predicate<SocialParserContext> predicate) {
        return SocialInjectedConditionalValue.of(value, conditionalParser, predicate);
    }

    static SocialInjectedLiteral literal(@NotNull TextComponent value) {
        return SocialInjectedLiteral.of(value);
    }

    static SocialInjectedLiteral literal(@NotNull String valueAsString) {
        return SocialInjectedLiteral.of(Component.text(valueAsString));
    }

    static SocialInjectedObject object(@NotNull String identifier, @NotNull Object value) {
        return SocialInjectedObject.of(identifier, value, SocialInjectionParsers.EMPTY);
    }

    static SocialInjectedObject object(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionEmptyParser parser) {
        return SocialInjectedObject.of(identifier, value, parser);
    }

    static SocialInjectedPlaceholder placeholder(@NotNull String identifier, @NotNull TextComponent value) {
        return SocialInjectedPlaceholder.of(identifier, value);
    }

    static SocialInjectedTag tag(@NotNull String identifier, @NotNull TagResolver value) {
        return SocialInjectedTag.of(identifier, value);
    }

    /** Returns the raw wrapped value. */
    @NotNull T value();

    /** Returns the parser for this exact concrete type — no cast required. */
    @NotNull SocialInjectionParser<Self> parser();

    /** Parses this value into a {@link Component} using the provided context. */
    @NotNull Component parse(@NotNull SocialParserContext context);

}