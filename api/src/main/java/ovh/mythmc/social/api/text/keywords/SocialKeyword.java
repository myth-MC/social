package ovh.mythmc.social.api.text.keywords;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.parsers.SocialPlayerInputParser;

import java.util.regex.Pattern;

public abstract class SocialKeyword implements SocialPlayerInputParser {

    public abstract String keyword();

    public abstract String process(SocialPlayer socialPlayer);

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        String processedString = process(socialPlayer);
        if (processedString == null || processedString.isEmpty())
            return message;
            
        Component processedText = MiniMessage.miniMessage().deserialize(process(socialPlayer));
        return message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(processedText)
                .build());
    }

}
