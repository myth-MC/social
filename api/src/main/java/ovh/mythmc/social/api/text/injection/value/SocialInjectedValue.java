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

public interface SocialInjectedValue<T> {

    static <T extends SocialInjectedValue<?>, P extends SocialInjectionParser<T>> SocialInjectedConditionalValue<T, P> conditional(@NotNull T value, @NotNull P conditionalParser, @NotNull Predicate<SocialParserContext> predicate) {
        return SocialInjectedConditionalValue.of(value, conditionalParser, predicate);
    }

    static SocialInjectedLiteral literal(@NotNull TextComponent value) {
        return SocialInjectedLiteral.of(value);
    }

    static SocialInjectedLiteral literal(@NotNull String valueAsString) {
        return literal(Component.text(valueAsString));
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

    @NotNull T value();

    @NotNull SocialInjectionParser<SocialInjectedValue<T>> parser();

    default Component parse(@NotNull SocialParserContext context) {
        return this.parser().parse(context, this);
    }

}