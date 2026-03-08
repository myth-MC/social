package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

/**
 * A {@link SocialInjectedValue} wrapping a 
 * {@link ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder}.
 * 
 * <p>
 * The placeholder is created dynamically with the given parameters.
 * </p>
 */
public class SocialInjectedPlaceholder extends AbstractSocialInjectedValue.Identified<TextComponent, SocialInjectedPlaceholder> {

    protected SocialInjectedPlaceholder(@NotNull String identifier, @NotNull TextComponent value) {
        super(identifier, value, SocialInjectionParsers.PLACEHOLDER);
    }

}
