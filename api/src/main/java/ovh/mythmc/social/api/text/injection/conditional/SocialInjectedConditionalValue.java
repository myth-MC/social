package ovh.mythmc.social.api.text.injection.conditional;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.AbstractSocialInjectedValue;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

import java.util.function.Predicate;

/**
 * A {@link SocialInjectedValue} that delegates to another value only when a
 * {@link Predicate} is satisfied.
 *
 * @param <T> the wrapped value type (must itself be a {@link SocialInjectedValue})
 * @param <P> the parser to use when the predicate is satisfied
 */
public class SocialInjectedConditionalValue<T extends SocialInjectedValue<?, T>, P extends SocialInjectionParser<T>>
        extends AbstractSocialInjectedValue<T, SocialInjectedConditionalValue<T, P>> {

    public static <T extends SocialInjectedValue<?, T>, P extends SocialInjectionParser<T>>
    SocialInjectedConditionalValue<T, P> of(@NotNull T value,
                                            @NotNull P conditionalParser,
                                            @NotNull Predicate<SocialParserContext> predicate) {
        return new SocialInjectedConditionalValue<>(value, conditionalParser, predicate);
    }

    private final P conditionalParser;
    private final Predicate<SocialParserContext> predicate;

    protected SocialInjectedConditionalValue(@NotNull T value,
                                             @NotNull P conditionalParser,
                                             @NotNull Predicate<SocialParserContext> predicate) {
        super(value, new SocialInjectionConditionalParser<>());
        this.conditionalParser = conditionalParser;
        this.predicate = predicate;
    }

    public @NotNull P conditionalParser() {
        return conditionalParser;
    }

    public @NotNull Predicate<SocialParserContext> predicate() {
        return predicate;
    }

}
