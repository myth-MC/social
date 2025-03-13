package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedObject;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInjectionEmptyParser implements SocialInjectionParser<SocialInjectedObject> {

    public static final SocialInjectionEmptyParser INSTANCE = new SocialInjectionEmptyParser();

    @Override
    public Component parse(@NotNull SocialParserContext context, @NotNull SocialInjectedObject value) {
        return context.message();
    }

}
