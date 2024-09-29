package ovh.mythmc.social.api.text.keywords;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialPlayerInputParser;

import java.util.regex.Pattern;

public abstract class SocialKeyword implements SocialPlayerInputParser {

    public abstract String keyword();

    public abstract String process(SocialPlayer socialPlayer);

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        return message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.compile("\\[(?i:" + keyword() + "\\b)\\]"))
                .replacement(process(socialPlayer))
                .build());
    }

}
