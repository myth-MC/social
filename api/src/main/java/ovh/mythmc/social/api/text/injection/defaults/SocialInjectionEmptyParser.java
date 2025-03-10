package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialInjectionEmptyParser implements SocialInjectionParser {

    public static final SocialInjectionEmptyParser INSTANCE = new SocialInjectionEmptyParser();

    @Override
    public Component parse(@NotNull SocialParserContext context) {
        return context.message();
    }

}
