package ovh.mythmc.social.api.text.parsers;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class SocialContextualKeyword implements SocialContextualParser {

    public abstract String keyword();

    public abstract Component process(SocialParserContext context);

    @Override
    public Component parse(SocialParserContext context) {      
        return context.message().replaceText(TextReplacementConfig.builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(process(context)
                    .applyFallbackStyle(Style.style((builder) -> builder.insertion("[" + keyword() + "] "))))
                .build());
    }
    
}
