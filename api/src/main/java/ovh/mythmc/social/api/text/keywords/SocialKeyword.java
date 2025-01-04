package ovh.mythmc.social.api.text.keywords;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialUserInputParser;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.regex.Pattern;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@Deprecated
@ScheduledForRemoval
public abstract class SocialKeyword implements SocialUserInputParser {

    public abstract String keyword();

    public abstract String process(SocialUser socialPlayer);

    @Override
    public Component parse(SocialParserContext context) {
        String processedString = process(context.user());
        if (processedString == null || processedString.isEmpty())
            return context.message();
            
        Component processedText = MiniMessage.miniMessage().deserialize(processedString);
        return context.message().replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(processedText)
                .build());
    }

}
