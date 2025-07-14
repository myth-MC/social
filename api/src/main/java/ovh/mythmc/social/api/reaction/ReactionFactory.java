package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.user.AbstractSocialUser;

public abstract class ReactionFactory {

    protected abstract void displayReaction(@NotNull AbstractSocialUser user, @NotNull Reaction reaction);

    public abstract void play(@NotNull AbstractSocialUser user, @NotNull Reaction reaction);

}
