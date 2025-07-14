package ovh.mythmc.social.api.text.injection.value;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;

public class SocialInjectedTag extends AbstractSocialInjectedValue.Identified<TagResolver> {

    public static SocialInjectedTag of(@NotNull String identifier, @NotNull TagResolver value) {
        return new SocialInjectedTag(identifier, value);
    }

    protected SocialInjectedTag(@NotNull String identifier, @NotNull TagResolver value) {
        super(identifier, value, SocialInjectionParsers.TAG);
    }

}
