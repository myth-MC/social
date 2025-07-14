package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedLiteral;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInjectionLiteralParser implements SocialInjectionParser<SocialInjectedLiteral> {

    public static final SocialInjectionLiteralParser INSTANCE = new SocialInjectionLiteralParser();

    @Override
    public Component parse(@NotNull SocialParserContext context, @NotNull SocialInjectedLiteral value) {
        return context.message().append(value.value());
    }

}
