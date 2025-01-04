package ovh.mythmc.social.api.text.parsers;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class SocialContextualKeyword implements SocialContextualParser {

    public abstract String keyword();

    public abstract Component process(SocialParserContext context);

    @Override
    public Component parse(SocialParserContext context) {
        if (!context.message().toString().contains(keyword()))
            return context.message();
        
        return context.message().replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(process(context).insertion("[" + keyword() + "] "))
                .build());
    }
    
}
