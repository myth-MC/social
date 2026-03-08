package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

/**
 * Base implementation of {@link SocialInjectedValue}.
 *
 * @param <T>    the wrapped value type
 * @param <Self> the concrete subtype
 */
public abstract class AbstractSocialInjectedValue<
        T,
        Self extends AbstractSocialInjectedValue<T, Self>
        > implements SocialInjectedValue<T, Self> {

    private final T value;
    private final SocialInjectionParser<Self> parser;

    protected AbstractSocialInjectedValue(
            @NotNull T value,
            @NotNull SocialInjectionParser<Self> parser
    ) {
        this.value = value;
        this.parser = parser;
    }

    @Override
    public @NotNull T value() {
        return this.value;
    }

    @Override
    public @NotNull SocialInjectionParser<Self> parser() {
        return this.parser;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Component parse(@NotNull SocialParserContext context) {
        // Safe by convention: subclasses bind Self to their own type
        return this.parser.parse(context, (Self) this);
    }

    /**
     * Variant that also carries a string identifier.
     */
    public abstract static class Identified<
            T,
            Self extends Identified<T, Self>
            > extends AbstractSocialInjectedValue<T, Self> {

        private final String identifier;

        protected Identified(
                @NotNull String identifier,
                @NotNull T value,
                @NotNull SocialInjectionParser<Self> parser
        ) {
            super(value, parser);
            this.identifier = identifier;
        }

        public @NotNull String identifier() {
            return this.identifier;
        }
    }
}