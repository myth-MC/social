package ovh.mythmc.social.api.emojis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class EmojiManager {

    public static final EmojiManager instance = new EmojiManager();

    private final Collection<Emoji> emojis = new ArrayList<>();

    public boolean registerEmoji(final @NotNull Emoji emoji) {
        return emojis.add(emoji);
    }

    public boolean unregisterEmoji(final @NotNull Emoji emoji) {
        return emojis.remove(emoji);
    }

    public Emoji get(String name) {
        for (Emoji emoji : emojis) {
            if (emoji.name().equalsIgnoreCase(name))
                return emoji;
        }

        return null;
    }

}
