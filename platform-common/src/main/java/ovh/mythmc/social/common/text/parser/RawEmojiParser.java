package ovh.mythmc.social.common.text.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.emoji.Emoji;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;

import java.util.regex.Pattern;

public final class RawEmojiParser implements SocialContextualParser {

    @Override
    public boolean supportsOfflinePlayers() {
        return true;
    }

    @Override
    public Component parse(SocialParserContext context) {
        Component message = context.message();

        for (Emoji emoji : Social.registries().emojis().values()) {
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
