package ovh.mythmc.social.api.emoji;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;

public record Emoji(String name, List<String> aliases, String unicodeCharacter) { 

    public Component asDescription(TextColor primaryColor, TextColor secondaryColor, boolean showClickToInsert) {
        Component description = Component.text(unicodeCharacter, primaryColor)
            .append(Component.text(" - ", secondaryColor))
            .append(Component.text("꞉" + name + "꞉", primaryColor));

        if (!aliases.isEmpty()) {
            String aliasesHoverText = String.format(
                Social.get().getConfig().getEmojis().getHoverTextAliases(),
                aliases.toString().replace("[", "").replace("]", ""));

            description = description
                .appendNewline()
                .append(MiniMessage.miniMessage().deserialize(aliasesHoverText));
        }

        if (showClickToInsert) {
            description = description
                .appendNewline()
                .appendNewline()
                .append(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getEmojis().getHoverTextInsertion()));
        }

        return description;
    }

}
