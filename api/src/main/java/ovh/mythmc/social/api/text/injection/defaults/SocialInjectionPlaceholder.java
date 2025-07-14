package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedPlaceholder;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInjectionPlaceholder implements SocialInjectionParser<SocialInjectedPlaceholder> {

    public static final SocialInjectionPlaceholder INSTANCE = new SocialInjectionPlaceholder();

    @Override
    public Component parse(@NotNull SocialParserContext context, @NotNull SocialInjectedPlaceholder value) {
        Component message = context.message();

        final var regexString = "\\$\\((?i:" + value.identifier() + "\\))";

        message = message.replaceText(TextReplacementConfig.builder()
            .match(Pattern.compile(regexString))
            .replacement((result) -> value.value())
            .build());

        return message;
    }

}
