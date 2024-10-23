package ovh.mythmc.social.api.text.parsers;

import static net.kyori.adventure.text.Component.text;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class SocialContextualPlaceholder implements SocialContextualParser {

    public abstract String identifier();

    public abstract Component get(SocialParserContext context);

    // Provides compatibility with legacy format
    // -> $channel, $username...
    public boolean legacySupport() { return false; }

    @Override
    public Component parse(SocialParserContext context) {
        Component processedText = get(context.withMessage(text(identifier())));
        Component message = context.message();

        // Legacy support (not delimited)
        if (legacySupport()) {
            message = message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\$(?i:" + identifier() + "\\b)"))
                .replacement(processedText)
                .build());
        }

        message = message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\$\\((?i:" + identifier() + "\\))"))
                .replacement(processedText)
                .build());

        return message;
    }
    
}
