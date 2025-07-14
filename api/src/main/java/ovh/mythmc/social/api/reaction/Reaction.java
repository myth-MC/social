package ovh.mythmc.social.api.reaction;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public interface Reaction {

    static Reaction.Builder<?> builder(@NotNull String name, @NotNull String texture) {
        return new ReactionImpl.BuilderImpl(name, texture);
    }

    @NotNull String name();

    @NotNull String texture();

    @Nullable Sound sound();

    @Nullable String particle();

    @NotNull List<String> triggerWords();

    interface Builder<T extends Builder<T>> {

        @NotNull T sound(@Nullable Sound sound);

        @NotNull T particle(@Nullable String particle);

        @NotNull T triggerWords(@NotNull List<String> triggerWords);

        default @NotNull T triggerWords(String... triggerWords) {
            return triggerWords(Arrays.asList(triggerWords));
        }

        @NotNull Reaction build();

    }

}
