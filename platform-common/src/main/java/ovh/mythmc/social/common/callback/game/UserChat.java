package ovh.mythmc.social.common.callback.game;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "message", getter = "message()"),
        @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class UserChat {

    private final SocialUser user;
    private final TextComponent message;
    private boolean cancelled = false;

    public UserChat(
        @NotNull SocialUser user,
        @NotNull TextComponent message
    ) {
        this.user = user;
        this.message = message;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull TextComponent message() {
        return this.message;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
