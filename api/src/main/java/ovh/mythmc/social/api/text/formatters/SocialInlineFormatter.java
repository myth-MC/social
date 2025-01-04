package ovh.mythmc.social.api.text.formatters;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public abstract class SocialInlineFormatter extends SocialFormatter {

    public abstract String characters();

    @Override
    public Pattern pattern() {
        return Pattern.compile("(?<!\\\\)" + Pattern.quote(characters()) + "([^\\\\]*)(\\\\*)");
    }

    @Override
    protected Component removeFormattingCharacters(Component component) {
        if (component instanceof TextComponent textComponent) {
            String content = textComponent.content().substring(characters().length());
            if (content.endsWith("\\"))
                content = content.substring(0, content.length() - 1);

            textComponent = textComponent.content(content.trim());
            return textComponent;
        }

        return component;  
    }

}