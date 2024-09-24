package ovh.mythmc.social.api.reactions;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.players.SocialPlayer;

public abstract class ReactionFactory {

    public static ReactionFactory reactionFactory;

    public static void set(final @NotNull ReactionFactory r) {
        reactionFactory = r;
    }

    public static @NotNull ReactionFactory get() { return reactionFactory; }

    public abstract void displayReaction(SocialPlayer player, Reaction emoji);

}
