package ovh.mythmc.social.api.text.injection.value;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

public abstract class AbstractSocialInjectedValue<T> implements SocialInjectedValue<T> {

    private final T value;

    private final SocialInjectionParser<? extends SocialInjectedValue<T>> parser;

    protected AbstractSocialInjectedValue(@NotNull T value, @NotNull SocialInjectionParser<? extends SocialInjectedValue<T>> parser) {
        this.value = value;
        this.parser = parser;
    }

    @Override
    public @NotNull T value() {
        return value;
    }

    @Override
    public @NotNull SocialInjectionParser<SocialInjectedValue<T>> parser() {
        return (SocialInjectionParser<SocialInjectedValue<T>>) parser;
    }

    public static abstract class Identified<T> extends AbstractSocialInjectedValue<T> {

        private final String identifier;

        protected Identified(@NotNull String identifier, @NotNull T value, @NotNull SocialInjectionParser<? extends SocialInjectedValue<T>> parser) {
            super(value, parser);
            this.identifier = identifier;
        }

        public String identifier() {
            return identifier;
        }

    }

}
