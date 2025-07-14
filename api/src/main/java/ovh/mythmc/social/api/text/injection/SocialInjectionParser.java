package ovh.mythmc.social.api.text.injection;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;

@ApiStatus.Experimental
@FunctionalInterface
public interface SocialInjectionParser<V extends SocialInjectedValue<?>> {

    Component parse(@NotNull SocialParserContext context, @NotNull V value);

}
