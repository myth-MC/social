package ovh.mythmc.social.api.callbacks.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "user", getter = "user()"),
    @CallbackFieldGetter(field = "channel", getter = "channel()"),
    @CallbackFieldGetter(field = "status", getter = "status()"),
    @CallbackFieldGetter(field = "cancelled", getter = "cancelled()")
})
public final class SocialUserMuteStatusChange {

    private final SocialUser user;

    private final ChatChannel channel;

    private final Boolean status;

    private boolean cancelled = false;
    
}
