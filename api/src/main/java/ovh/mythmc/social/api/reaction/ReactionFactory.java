package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.user.SocialUser;

public abstract class ReactionFactory {

    public static ReactionFactory reactionFactory;

    public static void set(final @NotNull ReactionFactory r) {
        reactionFactory = r;
    }

    public static @NotNull ReactionFactory get() { return reactionFactory; }

    protected abstract void displayReaction(SocialUser user, Reaction reaction);

    public abstract void scheduleReaction(SocialUser user, Reaction reaction);

}
