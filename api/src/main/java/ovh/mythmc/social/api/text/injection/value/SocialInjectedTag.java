package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

/**
 * A {@link SocialInjectedValue} wrapping a specific {@link TagResolver} applied
 * at runtime.
 * 
 * @see TagResolver
 */
public class SocialInjectedTag extends AbstractSocialInjectedValue.Identified<TagResolver, SocialInjectedTag> {

    protected SocialInjectedTag(@NotNull String identifier, @NotNull TagResolver value) {
        super(identifier, value, SocialInjectionParsers.TAG);
    }

}
