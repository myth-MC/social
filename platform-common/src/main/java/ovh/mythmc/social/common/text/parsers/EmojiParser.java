package ovh.mythmc.social.common.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.parsers.SocialPlayerInputParser;

import java.util.regex.Pattern;

@SocialParserProperties(priority = SocialParserProperties.ParserPriority.LOW)
public final class EmojiParser implements SocialPlayerInputParser {

    @Override
    public Component parse(SocialParserContext context) {
        Component message = context.message();

        for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
            StringBuilder aliases = new StringBuilder();
            for (String alias : emoji.aliases()) {
                aliases.append(formattedRegex(alias, true));
            }

            Pattern regex = Pattern.compile("(" + formattedRegex(emoji.name(), false) + aliases + ")");

            Component hoverText =
                    Component.text(emoji.unicodeCharacter(), NamedTextColor.YELLOW)
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("꞉" + emoji.name() + "꞉", NamedTextColor.YELLOW));

            if (!emoji.aliases().isEmpty()) {
                String aliasesHoverText = String.format(
                        Social.get().getConfig().getSettings().getEmojis().getHoverTextAliases(),
                        emoji.aliases().toString().replace("[", "").replace("]", ""));

                hoverText = hoverText
                        .appendNewline()
                        .append(MiniMessage.miniMessage().deserialize(aliasesHoverText));
            }

            hoverText = hoverText
                    .appendNewline()
                    .appendNewline()
                    .append(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getSettings().getEmojis().getHoverTextInsertion()));

            message = message.replaceText(TextReplacementConfig
                    .builder()
                    .match(regex)
                    .replacement(
                            Component.text(emoji.unicodeCharacter())
                                    .insertion(":" + emoji.name() + ": ")
                                    .hoverEvent(HoverEvent.showText(hoverText))
                    )
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
