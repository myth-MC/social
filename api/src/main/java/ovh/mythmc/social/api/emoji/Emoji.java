package ovh.mythmc.social.api.emoji;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public interface Emoji {

    static Builder<?> builder(@NotNull String name, @NotNull String unicodeCharacter) {
        return new EmojiImpl.BuilderImpl(name, unicodeCharacter);
    }

    @NotNull String name();

    List<String> aliases();

    @NotNull String unicodeCharacter();

    @NotNull Component asDescription(@NotNull TextColor primaryColor, @NotNull TextColor secondaryColor, boolean showClickToInsert);

    interface Builder<T extends Builder<T>> {

        @NotNull T aliases(@Nullable List<String> aliases);

        default @NotNull T aliases(String... aliases) {
            return aliases(Arrays.asList(aliases));
        }

        @NotNull Emoji build();

    }

}
