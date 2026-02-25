package ovh.mythmc.social.api.callback.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

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

    private @NotNull SocialUser user;

    private @Nullable ChatChannel channel;

    private boolean cancelled = false;

    public SocialChannelPreSwitch(@NotNull SocialUser user, @Nullable ChatChannel channel) {
        this.user = user;
        this.channel = channel;
    }

}
