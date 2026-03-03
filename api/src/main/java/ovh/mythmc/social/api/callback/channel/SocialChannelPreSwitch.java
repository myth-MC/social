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

/**
 * Event fired <em>before</em> a {@link SocialUser} switches their main {@link ChatChannel}.
 * 
 * <p>For the non-cancellable event fired <em>after</em> the user switches, see
 * {@link SocialChannelPostSwitch}.
 *
 * @param user      the {@link SocialUser} that switched their main channel
 * @param channel   the user's new main {@link ChatChannel}
 * @param cancelled whether the switch event is cancelled
 */
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "channel", getter = "channel()"),
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
