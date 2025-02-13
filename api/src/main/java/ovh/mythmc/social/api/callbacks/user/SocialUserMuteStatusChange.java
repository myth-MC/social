package ovh.mythmc.social.api.callbacks.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialUserMuteStatusChange {

    @CallbackFieldGetter("user")
    private final SocialUser user;

    @CallbackFieldGetter("channel")
    private final ChatChannel channel;

    @CallbackFieldGetter("status")
    private final Boolean status;

    @CallbackFieldGetter("cancelled")
    private boolean cancelled = false;
    
}
