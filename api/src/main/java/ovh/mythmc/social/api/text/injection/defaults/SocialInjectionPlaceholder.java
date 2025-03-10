package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParser;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialInjectionPlaceholder implements SocialInjectionParser {

    public static final SocialInjectionPlaceholder INSTANCE = new SocialInjectionPlaceholder();

    @Override
    public Component parse(@NotNull SocialParserContext context) {
        Component message = context.message();

        for (SocialParserContext.InjectedValue injectedValue : context.injectedValues()) {
            final var regexString = "\\$\\((?i:" + injectedValue.identifier() + "\\))";

            message = message.replaceText(TextReplacementConfig.builder()
                .match(Pattern.compile(regexString))
                .replacement((result) -> {
                    if (injectedValue.value() instanceof Component component)
                        return component;

                    return Component.text(injectedValue.value().toString());
                })
                .build());
        }

        return message;
    }

}
