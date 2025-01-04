package ovh.mythmc.social.api.reactions;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.users.SocialUser;

public abstract class ReactionFactory {

    public static ReactionFactory reactionFactory;

    public static void set(final @NotNull ReactionFactory r) {
        reactionFactory = r;
    }

    public static @NotNull ReactionFactory get() { return reactionFactory; }

    public abstract void displayReaction(SocialUser user, Reaction emoji);

}
