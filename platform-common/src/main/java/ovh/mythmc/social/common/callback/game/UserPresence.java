package ovh.mythmc.social.common.callback.game;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "type", getter = "type()"),
        @CallbackField(field = "message", getter = "message()")
})
public final class UserPresence {

    private Optional<? extends SocialUser> user;
    private final UserPresence.Type type;
    private Optional<Component> message;

    public UserPresence(
        @NotNull Optional<? extends SocialUser> user,
        @NotNull UserPresence.Type type,
        @NotNull Optional<Component> message
    ) {
        this.user = user;
        this.type = type;
        this.message = message;
    }

    public @NotNull Optional<? extends SocialUser> user() {
        return this.user;
    }

    public @NotNull UserPresence.Type type() {
        return this.type;
    }

    public @NotNull Optional<Component> message() {
        return this.message;
    }

    public void user(@NotNull Optional<? extends SocialUser> user) {
        this.user = user;
    }

    public void message(@NotNull Optional<Component> message) {
        this.message = message;
    }

    public enum Type {
        LOGIN,
        JOIN,
        QUIT
    }

}
