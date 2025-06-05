package ovh.mythmc.social.api.emoji;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;

final class EmojiImpl implements Emoji {

    private final String name;

    private final List<String> aliases;

    private final String unicodeCharacter;

    private EmojiImpl(String name, List<String> aliases, String unicodeCharacter) {
        this.name = name;
        this.aliases = aliases;
        this.unicodeCharacter = unicodeCharacter;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public List<String> aliases() {
        return this.aliases;
    }

    @Override
    public @NotNull String unicodeCharacter() {
        return this.unicodeCharacter;
    }

    @Override
    public @NotNull Component asDescription(@NotNull TextColor primaryColor, @NotNull TextColor secondaryColor, boolean showClickToInsert) {
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

    static final class BuilderImpl implements Emoji.Builder<BuilderImpl> {

        private final String name;

        private final String unicodeCharacter;

        private List<String> aliases;

        BuilderImpl(@NotNull String name, @NotNull String unicodeCharacter) {
            this.name = name;
            this.unicodeCharacter = unicodeCharacter;
            this.aliases = new ArrayList<>();
        }

        @Override
        public @NotNull BuilderImpl aliases(@Nullable List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        @Override
        public @NotNull Emoji build() {
            return new EmojiImpl(name, aliases, unicodeCharacter);
        }

    }

}
