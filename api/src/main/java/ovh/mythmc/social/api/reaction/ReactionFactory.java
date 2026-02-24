package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.user.SocialUser;

public abstract class ReactionFactory {

    protected abstract void displayReaction(@NotNull SocialUser user, @NotNull Reaction reaction);

    public abstract void play(@NotNull SocialUser user, @NotNull Reaction reaction);

}
