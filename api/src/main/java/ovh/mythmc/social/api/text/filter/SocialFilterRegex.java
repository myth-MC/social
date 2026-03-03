package ovh.mythmc.social.api.text.filter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;

import java.util.regex.Pattern;

/**
 * Abstract parser capable of censoring text by matching a given regular expression.
 * 
 * <p>
 * Any text matching {@code regex()} will be filtered out and replaced
 * with {@code ***}.
 * </p>
 */
public abstract class SocialFilterRegex implements SocialFilterLike {

    public abstract String regex();

    @Override
    public Component parse(SocialParserContext context) {
        if (context.user().checkPermission("social.filter.bypass"))
            return context.message();

        return context.message().replaceText(TextReplacementConfig.builder()
                .match(Pattern.compile(regex()))
                .replacement("***")
                .build());
    }

}
