package ovh.mythmc.social.api.reaction;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Represents an interactive reaction that can be triggered.
 *
 * <p>
 * A {@code Reaction} may include a visual representation (texture), an optional
 * sound, particle effect, and a set of trigger words that activate it.
 * </p>
 *
 * <p>
 * Instances of {@code Reaction} are created using the {@link #builder(String, String)}
 * method.
 * </p>
 *
 * @see Reactable
 */
public interface Reaction {

    /**
     * Creates a new {@link Builder} for constructing a {@link Reaction}.
     *
     * @param name    the unique name of the reaction
     * @param texture the texture or visual representation of the reaction
     * @return a new reaction builder
     */
    static Reaction.Builder<?> builder(@NotNull String name, @NotNull String texture) {
        return new ReactionImpl.BuilderImpl(name, texture);
    }

    /**
     * Returns the unique name of this reaction.
     *
     * @return the reaction name
     */
    @NotNull
    String name();

    /**
     * Returns the texture associated with this reaction.
     *
     * @return the texture string
     */
    @NotNull
    String texture();

    /**
     * Returns the optional sound associated with this reaction.
     *
     * @return the {@link Sound}, or {@code null} if none
     */
    @Nullable
    Sound sound();

    /**
     * Returns the optional particle effect associated with this reaction.
     *
     * @return the particle effect name, or {@code null} if none
     */
    @Nullable
    String particle();

    /**
     * Returns the list of words that trigger this reaction.
     *
     * @return a list of trigger words
     */
    @NotNull
    List<String> triggerWords();

    /**
     * Builder for creating {@link Reaction} instances.
     *
     * @param <T> the concrete builder type
     */
    interface Builder<T extends Builder<T>> {

        /**
         * Sets the sound for this reaction.
         *
         * @param sound the sound to play, or {@code null} for none
         * @return this builder
         */
        @NotNull
        T sound(@Nullable Sound sound);

        /**
         * Sets the particle effect for this reaction.
         *
         * @param particle the particle effect name, or {@code null} for none
         * @return this builder
         */
        @NotNull
        T particle(@Nullable String particle);

        /**
         * Sets the trigger words for this reaction.
         *
         * @param triggerWords the list of trigger words
         * @return this builder
         */
        @NotNull
        T triggerWords(@NotNull List<String> triggerWords);

        /**
         * Sets the trigger words for this reaction.
         *
         * @param triggerWords the trigger words as varargs
         * @return this builder
         */
        default @NotNull T triggerWords(String... triggerWords) {
            return triggerWords(Arrays.asList(triggerWords));
        }

        /**
         * Builds the {@link Reaction} instance.
         *
         * @return the constructed reaction
         */
        @NotNull
        Reaction build();

    }
}