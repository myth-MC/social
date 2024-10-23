package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.regex.Pattern;

public abstract class SocialPlaceholder implements SocialContextualParser {

    public abstract String identifier();

    public abstract String process(SocialPlayer player);

    public boolean legacySupport() { return false; }

    @Override
    public Component parse(SocialParserContext context) {
        Component processedText = MiniMessage.miniMessage().deserialize(process(context.socialPlayer()));
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
