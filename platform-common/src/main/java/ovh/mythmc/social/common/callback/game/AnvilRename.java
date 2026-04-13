package ovh.mythmc.social.common.callback.game;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "name", getter = "name()")
})
public final class AnvilRename {

    private final SocialUser user;
    private Component name;

    public AnvilRename(
        @NotNull SocialUser user,
        @NotNull Component name
    ) {
        this.user = user;
        this.name = name;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull Component name() {
        return this.name;
    }

    public void name(@NotNull Component name) {
        this.name = name;
    }

}
