package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.user.SocialUser;

/**
 * Abstract factory for handling and displaying {@link Reaction} instances.
 *
 * <p>
 * A {@code ReactionFactory} is responsible for executing reactions for
 * specific {@link SocialUser users}, including both visual and interactive effects.
 * </p>
 *
 * <p>
 * Subclasses must implement the actual behavior for displaying and playing
 * reactions according to the platform or environment.
 * </p>
 *
 * @see Reaction
 * @see SocialUser
 */
public abstract class ReactionFactory {

    /**
     * Displays a reaction to a specific user.
     *
     * <p>
     * This method is intended to handle the visual or UI aspects of a reaction,
     * such as showing textures, particles, or other effects.
     * </p>
     *
     * @param user     the user for whom the reaction is displayed
     * @param reaction the reaction to display
     */
    protected abstract void displayReaction(@NotNull SocialUser user, @NotNull Reaction reaction);

    /**
     * Plays a reaction for a specific user.
     *
     * <p>
     * This method triggers the full reaction behavior, including
     * visual, auditory, or interactive effects, and may call
     * {@link #displayReaction(SocialUser, Reaction)} internally.
     * </p>
     *
     * @param user     the user who receives the reaction
     * @param reaction the reaction to play
     */
    public abstract void play(@NotNull SocialUser user, @NotNull Reaction reaction);

}