package ovh.mythmc.social.api.text.injection.conditional;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.AbstractSocialInjectedValue;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

import java.util.function.Predicate;

public class SocialInjectedConditionalValue<T extends SocialInjectedValue<?>, P extends SocialInjectionParser<T>> extends AbstractSocialInjectedValue<T> {

    public static <T extends SocialInjectedValue<?>, P extends SocialInjectionParser<T>> SocialInjectedConditionalValue<T, P> of(@NotNull T value, @NotNull P conditionalParser, @NotNull Predicate<SocialParserContext> predicate) {
        return new SocialInjectedConditionalValue<>(value, conditionalParser, predicate);
    }

    private final P conditionalParser;

    private final Predicate<SocialParserContext> predicate;

    protected SocialInjectedConditionalValue(@NotNull T value, @NotNull P conditionalParser, @NotNull Predicate<SocialParserContext> predicate) {
        super(value, SocialInjectionConditionalParser.of(value));
        this.conditionalParser = conditionalParser;
        this.predicate = predicate;
    }

    public P conditionalParser() {
        return conditionalParser;
    }

    public Predicate<SocialParserContext> predicate() {
        return predicate;
    }

}
