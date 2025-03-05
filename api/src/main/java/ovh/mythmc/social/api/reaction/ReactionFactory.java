package ovh.mythmc.social.api.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.user.AbstractSocialUser;

public abstract class ReactionFactory<U extends AbstractSocialUser<? extends Object>> {

    protected abstract void displayReaction(@NotNull U user, @NotNull Reaction reaction);

    public abstract void scheduleReaction(@NotNull U user, @NotNull Reaction reaction);

}
