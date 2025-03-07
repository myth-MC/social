package ovh.mythmc.social.api.callback.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "user", getter = "user()"),
    @CallbackField(field = "channel", getter = "channel()"),
    @CallbackField(field = "status", getter = "status()"),
    @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class SocialUserMuteStatusChange {

    private final AbstractSocialUser user;

    private final ChatChannel channel;

    private final Boolean status;

    private boolean cancelled = false;
    
}
