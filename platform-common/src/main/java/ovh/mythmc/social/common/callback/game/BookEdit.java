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
        @CallbackField(field = "pages", getter = "pages()")
})
public class BookEdit {

    private final SocialUser user;
    private List<Component> pages;

    public BookEdit(
        @NotNull SocialUser user,
        @NotNull List<Component> pages
    ) {
        this.user = user;
        this.pages = pages;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull List<Component> pages() {
        return this.pages;
    }

    public void pages(@NotNull List<Component> pages) {
        this.pages = pages;
    }

}
