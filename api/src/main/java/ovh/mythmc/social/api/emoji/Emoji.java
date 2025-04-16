package ovh.mythmc.social.api.emoji;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;

public final class Emoji {

    public static Builder builder(@NotNull String name, @NotNull String unicodeCharacter) {
        return new Builder(name, unicodeCharacter);
    }

    private final String name;

    private final List<String> aliases;

    private final String unicodeCharacter;

    private Emoji(String name, List<String> aliases, String unicodeCharacter) {
        this.name = name;
        this.aliases = aliases;
        this.unicodeCharacter = unicodeCharacter;
    }

    public String name() {
        return this.name;
    }

    public List<String> aliases() {
        return this.aliases;
    }

    public String unicodeCharacter() {
        return this.unicodeCharacter;
    }

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

    public static final class Builder {

        private final String name;

        private final String unicodeCharacter;

        private List<String> aliases;

        private Builder(@NotNull String name, @NotNull String unicodeCharacter) {
            this.name = name;
            this.unicodeCharacter = unicodeCharacter;
            this.aliases = new ArrayList<>();
        }

        public Builder aliases(@Nullable List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public Emoji build() {
            return new Emoji(name, aliases, unicodeCharacter);
        }

    }

}
