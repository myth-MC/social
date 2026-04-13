package ovh.mythmc.social.common.callback.game;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "lines", getter = "lines()")
})
public final class SignEdit {

    private final SocialUser user;
    private List<Component> lines;

    public SignEdit(
        @NotNull SocialUser user,
        @NotNull List<Component> lines
    ) {
        this.user = user;
        this.lines = lines;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull List<Component> lines() {
        return this.lines;
    }

    public void lines(@NotNull List<Component> lines) {
        this.lines = lines;
    }

}
