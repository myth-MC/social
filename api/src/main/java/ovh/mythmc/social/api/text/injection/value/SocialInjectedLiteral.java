package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

/**
 * A {@link SocialInjectedValue} that wraps a specific {@link TextComponent}.
 */
public class SocialInjectedLiteral extends AbstractSocialInjectedValue<TextComponent, SocialInjectedLiteral> {

    protected SocialInjectedLiteral(@NotNull TextComponent value) {
        super(value, SocialInjectionParsers.LITERAL);
    }

}
