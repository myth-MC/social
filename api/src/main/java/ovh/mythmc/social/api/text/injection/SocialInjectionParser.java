package ovh.mythmc.social.api.text.injection;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

/**
 * Represents a class capable of parsing a specific type of {@link SocialInjectedValue}.
 */
@ApiStatus.Experimental
@FunctionalInterface
public interface SocialInjectionParser<V extends SocialInjectedValue<?, V>> {

    /**
     * Parses a {@link SocialParserContext} for the given {@link V} and returns
     * the result.
     * @param context the {@link SocialParserContext} to parse
     * @param value   the {@link V} to apply
     * @return        a {@link Component} with the injected value applied
     */
    Component parse(@NotNull SocialParserContext context, @NotNull V value);

}
