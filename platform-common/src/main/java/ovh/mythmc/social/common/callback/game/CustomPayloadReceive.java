package ovh.mythmc.social.common.callback.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
    @CallbackField(field = "channel", getter = "channel()"),
    @CallbackField(field = "payload", getter = "payload()")
})
public final class CustomPayloadReceive {

    private final AbstractSocialUser user;

    private final String channel;

    private final byte[] payload;
    
}
