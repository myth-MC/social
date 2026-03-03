package ovh.mythmc.social.api.text.injection.value;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;

/**
 * A {@link SocialInjectedValue} that wraps a specific {@link Object}.
 * 
 * <p>
 * The default {@link SocialInjectionEmptyParser} implementation doesn't modify
 * the message in any way, meaning that injected objects are just a way of passing
 * objects between parsers and contexts.
 * </p>
 */
public class SocialInjectedObject extends AbstractSocialInjectedValue.Identified<Object, SocialInjectedObject> {

    protected SocialInjectedObject(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionEmptyParser parser) {
        super(identifier, value, parser);
    }

}
