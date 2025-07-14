package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedTag;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInjectionTagParser implements SocialInjectionParser<SocialInjectedTag> {

    public static final SocialInjectionTagParser INSTANCE = new SocialInjectionTagParser();

    @Override
    public Component parse(@NotNull SocialParserContext context, @NotNull SocialInjectedTag value) {
        return context.message();
    }

}
