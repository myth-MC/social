package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

public class SocialInjectedPlaceholder extends AbstractSocialInjectedValue.Identified<TextComponent> {

    public static SocialInjectedPlaceholder of(@NotNull String identifier, @NotNull TextComponent value) {
        return new SocialInjectedPlaceholder(identifier, value);
    }

    protected SocialInjectedPlaceholder(@NotNull String identifier, @NotNull TextComponent value) {
        super(identifier, value, SocialInjectionParsers.PLACEHOLDER);
    }

}
