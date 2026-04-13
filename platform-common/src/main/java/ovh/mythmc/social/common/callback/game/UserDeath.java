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
        @CallbackField(field = "deathMessage", getter = "deathMessage()")
})
public final class UserDeath {

    private final SocialUser user;
    private Component deathMessage;

    public UserDeath(
        @NotNull SocialUser user,
        @NotNull Component deathMessage
    ) {
        this.user = user;
        this.deathMessage = deathMessage;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull Component deathMessage() {
        return this.deathMessage;
    }

    public void deathMessage(@NotNull Component deathMessage) {
        this.deathMessage = deathMessage;
    }

}
