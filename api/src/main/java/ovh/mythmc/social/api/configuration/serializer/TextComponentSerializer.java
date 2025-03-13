package ovh.mythmc.social.api.configuration.serializer;

import de.exlll.configlib.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public final class TextComponentSerializer implements Serializer<TextComponent, String> {

    @Override
    public String serialize(TextComponent textComponent) {
        return textComponent.content();
    }

    @Override
    public TextComponent deserialize(String s) {
        return Component.text(s);
    }

}
