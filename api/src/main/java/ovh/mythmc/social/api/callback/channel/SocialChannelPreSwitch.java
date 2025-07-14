package ovh.mythmc.social.api.callback.channel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "user", getter = "user()"),
    @CallbackField(field = "channel", getter = "channel()"),
    @CallbackField(field = "informUser", getter = "informUser()"),
    @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class SocialChannelPreSwitch {

    private @NotNull AbstractSocialUser user;

    private @NotNull ChatChannel channel;

    private @NotNull boolean informUser;

    private boolean cancelled = false;
    
}
