package ovh.mythmc.social.api.reaction;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ReactionImpl implements Reaction {

    private final @NotNull String name;

    private final @NotNull String texture;

    private final @Nullable Sound sound;

    private final @Nullable String particle;

    private final @NotNull List<String> triggerWords;

    private ReactionImpl(@NotNull String name, @NotNull String texture, @Nullable Sound sound, @Nullable String particle, @NotNull List<String> triggerWords) {
        this.name = name;
        this.texture = texture;
        this.sound = sound;
        this.particle = particle;
        this.triggerWords = triggerWords;
    }

    @Override
    public @NotNull String name() { return this.name; }

    @Override
    public @NotNull String texture() { return this.texture; }

    @Override
    public Sound sound() { return this.sound; }

    @Override
    public String particle() { return this.particle; }

    @Override
    public @NotNull List<String> triggerWords() { return this.triggerWords; }

    static final class BuilderImpl implements Reaction.Builder<BuilderImpl> {

        private final String name;

        private final String texture;

        private Sound sound;

        private String particle;

        private List<String> triggerWords;

        BuilderImpl(@NotNull String name, @NotNull String texture) {
            this.name = name;
            this.texture = texture;
            this.triggerWords = new ArrayList<>();
        }

        @Override
        public @NotNull BuilderImpl sound(@Nullable Sound sound) {
            this.sound = sound;
            return this;
        }

        @Override
        public @NotNull BuilderImpl particle(@Nullable String particle) {
            this.particle = particle;
            return this;
        }

        @Override
        public @NotNull BuilderImpl triggerWords(@NotNull List<String> triggerWords) {
            this.triggerWords = triggerWords;
            return this;
        }

        @Override
        public @NotNull Reaction build() {
            return new ReactionImpl(name, texture, sound, particle, triggerWords);
        }

    }

}
