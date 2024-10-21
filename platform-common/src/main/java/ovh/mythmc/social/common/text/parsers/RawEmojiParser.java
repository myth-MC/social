package ovh.mythmc.social.common.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.parsers.SocialParser;

import java.util.regex.Pattern;

@SocialParserProperties(priority = SocialParserProperties.ParserPriority.LOW)
public final class RawEmojiParser implements SocialParser {

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
            StringBuilder aliases = new StringBuilder();
            for (String alias : emoji.aliases()) {
                aliases.append(formattedRegex("raw_" + alias, true));
            }

            Pattern regex = Pattern.compile("(" + formattedRegex("raw_" + emoji.name(), false) + aliases + ")");

            message = message.replaceText(TextReplacementConfig
                    .builder()
                    .match(regex)
                    .replacement(emoji.unicodeCharacter())
                    .build());
        }

        return message;
    }

    private String formattedRegex(String input, boolean divider) {
        String regex = ":(?i:" + input + "):";

        if (divider)
            return "|" + regex;
        return regex;
    }

}
