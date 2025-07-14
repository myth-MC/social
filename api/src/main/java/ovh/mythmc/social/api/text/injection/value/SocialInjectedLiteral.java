package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

public class SocialInjectedLiteral extends AbstractSocialInjectedValue<TextComponent> {

    public static SocialInjectedLiteral of(@NotNull TextComponent value) {
        return new SocialInjectedLiteral(value);
    }

    protected SocialInjectedLiteral(@NotNull TextComponent value) {
        super(value, SocialInjectionParsers.LITERAL);
    }

}
