package ovh.mythmc.social.api.text.injection.value;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;

public class SocialInjectedObject extends AbstractSocialInjectedValue.Identified<Object> {

    public static SocialInjectedObject of(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionEmptyParser parser) {
        return new SocialInjectedObject(identifier, value, parser);
    }

    protected SocialInjectedObject(@NotNull String identifier, @NotNull Object value, @NotNull SocialInjectionEmptyParser parser) {
        super(identifier, value, parser);
    }

}
