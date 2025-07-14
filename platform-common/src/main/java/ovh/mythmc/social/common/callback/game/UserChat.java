package ovh.mythmc.social.common.callback.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "user", getter = "user()"),
    @CallbackField(field = "message", getter = "message()"),
    @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class UserChat {

    private final AbstractSocialUser user;

    private final TextComponent message;

    private boolean cancelled = false;
    
}
