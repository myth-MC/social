package ovh.mythmc.social.api.callback.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired <em>before</em> a {@link SocialUser} switches their main {@link ChatChannel}.
 * <p>
 * This event allows for modification or cancellation before the user's main channel is switched.
 * If the event is cancelled, the user will not be switched to the new channel.
 * </p>
 * <p>For the non-cancellable event fired <em>after</em> the user switches, see {@link SocialChannelPostSwitch}.</p>
 * 
 * <p>Fields:</p>
 * <ul>
 *     <li><b>user:</b> The {@link SocialUser} that switched their main channel.</li>
 *     <li><b>channel:</b> The new main {@link ChatChannel} that the user is switching to. It can be {@code null}.</li>
 *     <li><b>cancelled:</b> Whether the event has been cancelled, preventing the switch from happening.</li>
 * </ul>
 */
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

    /**
     * Constructs a new {@code SocialChannelPreSwitch} event with the specified user and channel.
     * 
     * @param user the {@link SocialUser} who is switching their main channel
     * @param channel the new main {@link ChatChannel} that the user is switching to, or {@code null} if no channel is set
     */
    public SocialChannelPreSwitch(@NotNull SocialUser user, @Nullable ChatChannel channel) {
        this.user = user;
        this.channel = channel;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @Nullable ChatChannel channel() {
        return this.channel;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void channel(@Nullable ChatChannel channel) {
        this.channel = channel;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


}