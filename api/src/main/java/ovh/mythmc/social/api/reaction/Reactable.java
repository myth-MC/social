package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object capable of handling and playing a {@link Reaction}.
 *
 * <p>
 * A {@code Reactable} can display reactions to other users.
 * </p>
 *
 * @see Reaction
 */
public interface Reactable {

    /**
     * Plays or triggers the specified {@link Reaction}.
     *
     * <p>
     * The exact behavior depends on the implementation and may
     * include visual, auditory, or other interactive effects.
     * </p>
     *
     * @param reaction the reaction to play
     */
    void playReaction(@NotNull Reaction reaction);

}