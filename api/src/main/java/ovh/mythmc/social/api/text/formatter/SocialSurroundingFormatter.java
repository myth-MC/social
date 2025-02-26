package ovh.mythmc.social.api.text.formatter;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;

public abstract class SocialSurroundingFormatter extends SocialFormatter {
    
    public abstract String characters();

    @Override
    public Pattern pattern() {
        return Pattern.compile("(?<!\\\\)" + Pattern.quote(characters()) + "(.*)" + Pattern.quote(characters()));
    }

    @Override
    public Component parse(SocialParserContext context) {
        if (characters().equalsIgnoreCase(":")) {
            Social.get().getLogger().info("Formatter contains an illegal character! ({})", characters());
            return context.message();
        }

        return super.parse(context);
    }

    @Override
    protected Component removeFormattingCharacters(Component component) {
        if (component instanceof TextComponent textComponent) {
            var content = textComponent.content().trim();
            textComponent = textComponent.content(content.substring(characters().length(), content.length() - characters().length()));
            return textComponent;
        }

        return component;
    }

}
