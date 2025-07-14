package ovh.mythmc.social.api.text.injection.conditional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialInjectionConditionalParser<T extends SocialInjectedValue<?>, P extends SocialInjectionParser<T>> implements SocialInjectionParser<SocialInjectedConditionalValue<T, P>> {

    public static @NotNull <T extends SocialInjectedValue<?>> SocialInjectionParser<? extends SocialInjectedValue<T>> of(@NotNull T value) {
        return new SocialInjectionConditionalParser<>();
    }

    @Override
    public Component parse(@NotNull SocialParserContext context, @NotNull SocialInjectedConditionalValue<T, P> conditionalValue) {
        Component message = context.message();

        if (conditionalValue.predicate().test(context))
            message = conditionalValue.value().parse(context);

        return message;
    }

}
