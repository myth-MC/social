package ovh.mythmc.social.api.text.injection.conditional;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

public class SocialInjectionConditionalParser<T extends SocialInjectedValue<?, T>, P extends SocialInjectionParser<T>>
        implements SocialInjectionParser<SocialInjectedConditionalValue<T, P>> {

    @Override
    public Component parse(@NotNull SocialParserContext context,
                           @NotNull SocialInjectedConditionalValue<T, P> conditionalValue) {
        if (conditionalValue.predicate().test(context))
            return conditionalValue.value().parse(context);

        return context.message();
    }

}
