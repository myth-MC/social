package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.conditional.SocialInjectedConditionalValue;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

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

    /**
     * Creates a new {@link SocialInjectedLiteral} wrapping a {@link TextComponent}.
     * @param value the {@link TextComponent} to wrap
     * @return      a {@link SocialInjectedLiteral} wrapping {@code value}
     */
    static SocialInjectedLiteral literal(@NotNull TextComponent value) {
        return new SocialInjectedLiteral(value);
    }

    /**
     * Creates a new {@link SocialInjectedLiteral} wrapping a {@link TextComponent} built
     * from the given {@link String}.
     * @param value the {@link String} to build into a {@link TextComponent} and wrap
     * @return      a {@link SocialInjectedLiteral} wrapping {@code value}
     */
    static SocialInjectedLiteral literal(@NotNull String valueAsString) {
        return literal(Component.text(valueAsString));
    }

    /**
     * Creates a new {@link SocialInjectedObject} wrapping a specific {@link Object}.
     * @param identifier the identifier for the wrapped object
     * @param value      the object to wrap
     * @param parser     the {@link SocialInjectionEmptyParser} to parse the wrapped object
     * @return           a {@link SocialInjectedObject} wrapping {@code value}
     */
    static SocialInjectedObject object(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionEmptyParser parser) {
        return new SocialInjectedObject(identifier, value, parser);
    }

    /**
     * Creates a new {@link SocialInjectedObject} wrapping a specific {@link Object}.
     * 
     * <p>
     * The injected object will use the default {@link SocialInjectionEmptyParser}
     * implementation.
     * </p>
     * @param identifier the identifier for the wrapped object
     * @param value      the object to wrap
     * @return           a {@link SocialInjectedObject} wrapping {@code value}
     */
    static SocialInjectedObject object(@NotNull String identifier, @NotNull Object value) {
        return object(identifier, value, SocialInjectionEmptyParser.INSTANCE);
    }

    /**
     * Creates a new {@link SocialInjectedPlaceholder} with the given parameters.
     * 
     * <p>
     * The {@link SocialInjectedPlaceholder} will be built into a {@link SocialContextualPlaceholder}
     * at runtime from the given parameters.
     * </p>
     * @param identifier the identifier for the wrapped placeholder
     * @param value      the {@link TextComponent} that the placeholder will return
     * @return           a {@link SocialInjectedPlaceholder} wrapping the placeholder
     */
    static SocialInjectedPlaceholder placeholder(@NotNull String identifier, @NotNull TextComponent value) {
        return new SocialInjectedPlaceholder(identifier, value);
    }

    /**
     * Creates a new {@link SocialInjectedTag} wrapping a {@link TagResolver}.
     * @param identifier the identifier for the wrapped tag
     * @param value      the {@link TagResolver} to wrap
     * @return           a {@link SocialInjectedTag} wrapping the {@link TagResolver}
     */
    static SocialInjectedTag tag(@NotNull String identifier, @NotNull TagResolver value) {
        return new SocialInjectedTag(identifier, value);
    }

    /** Returns the raw wrapped value. */
    @NotNull T value();

    /** Returns the parser for this exact concrete type — no cast required. */
    @NotNull SocialInjectionParser<Self> parser();

    /** Parses this value into a {@link Component} using the provided context. */
    @NotNull Component parse(@NotNull SocialParserContext context);

}