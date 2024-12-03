package ovh.mythmc.social.api.emojis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class EmojiManager {

    public static final EmojiManager instance = new EmojiManager();

    private final Map<Emoji, String> emojis = new HashMap<>();

    public void registerEmoji(@NotNull String category, @NotNull Emoji emoji) {
        emojis.put(emoji, category);
    }

    public void unregisterEmoji(@NotNull Emoji emoji) {
        emojis.remove(emoji);
    }

    public void unregisterAll() {
        emojis.clear();
    }

    public List<Emoji> getEmojis() {
        return emojis.keySet().stream().toList();
    }

    public Emoji getByName(@NotNull String name) {
        for (Emoji emoji : emojis.keySet()) {
            if (emoji.name().equalsIgnoreCase(name))
                return emoji;
        }

        return null;
    }

    public String getCategory(@NotNull Emoji emoji) {
        return emojis.get(emoji);
    }

    public List<Emoji> getByCategory(@NotNull String category) {
        return emojis.entrySet().stream().filter(entry -> entry.getValue().equals(category)).map(entry -> entry.getKey()).collect(Collectors.toList());
    }

}
