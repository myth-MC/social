package ovh.mythmc.social.api.reaction;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Reaction {

    public static Builder builder(@NotNull String name, @NotNull String texture) {
        return new Builder(name, texture);
    }

    private final @NotNull String name;

    private final @NotNull String texture;

    private final @Nullable Sound sound;

    private final @Nullable String particle;

    private final @NotNull List<String> triggerWords;

    private Reaction(@NotNull String name, @NotNull String texture, @Nullable Sound sound, @Nullable String particle, @NotNull List<String> triggerWords) {
        this.name = name;
        this.texture = texture;
        this.sound = sound;
        this.particle = particle;
        this.triggerWords = triggerWords;
    }

    public String name() { return this.name; }

    public String texture() { return this.texture; }

    public Sound sound() { return this.sound; }

    public String particle() { return this.particle; }

    public List<String> triggerWords() { return this.triggerWords; }

    public static final class Builder {

        private final String name;

        private final String texture;

        private Sound sound;

        private String particle;

        private List<String> triggerWords;

        private Builder(@NotNull String name, @NotNull String texture) {
            this.name = name;
            this.texture = texture;
            this.triggerWords = new ArrayList<>();
        }

        public Builder sound(@Nullable Sound sound) {
            this.sound = sound;
            return this;
        }

        public Builder particle(@Nullable String particle) {
            this.particle = particle;
            return this;
        }

        public Builder triggerWords(@NotNull List<String> triggerWords) {
            this.triggerWords = triggerWords;
            return this;
        }

        public Builder triggerWord(@NotNull String triggerWord) {
            this.triggerWords.add(triggerWord);
            return this;
        }

        public Reaction build() {
            return new Reaction(name, texture, sound, particle, triggerWords);
        }

    }

}
