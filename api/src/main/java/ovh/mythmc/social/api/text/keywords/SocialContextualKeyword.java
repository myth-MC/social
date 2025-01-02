package ovh.mythmc.social.api.text.keywords;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;

@SuppressWarnings("deprecation") // Compatibility reasons
public abstract class SocialContextualKeyword extends SocialKeyword {

    public abstract String keyword();

    public abstract Component process(SocialParserContext context);

    @Override
    public String process(SocialUser socialPlayer) {
        return "";
    }

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
