package ovh.mythmc.social.common.callback.game;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "user", getter = "user()"),
    @CallbackField(field = "type", getter = "type()")
})
public final class UserPresence {

    private Optional<? extends AbstractSocialUser<?>> user;

    private final UserPresence.Type type;

    private Optional<Component> message;

    public enum Type {
        LOGIN, 
        JOIN, 
        QUIT
    }
    
}
