package ovh.mythmc.social.api.text.parser;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class SocialContextualPlaceholder implements SocialContextualParser {

    public abstract String identifier();

    public abstract Component get(SocialParserContext context);

    @Deprecated(since = "0.4")
    public boolean legacySupport() { return false; }

    @Override
    public Component parse(SocialParserContext context) {
        var regexString = "\\$\\((?i:" + identifier() + "\\))";
        if (legacySupport())
            regexString = regexString + "|\\$(?i:" + identifier() + "\\b)";

        return context.message().replaceText(TextReplacementConfig.builder()
            .match(Pattern.compile(regexString))
            .replacement(get(context.withMessage(Component.empty())))
            .build()
        );
    }
    
}
