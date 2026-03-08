package ovh.mythmc.social.api.callback.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired before a {@link SocialUser}'s mute status changes in a {@link ChatChannel}.
 * <p>
 * This event is fired before the mute status of a {@link SocialUser} is changed in a given
 * {@link ChatChannel}. The event allows modification or cancellation of the mute status change.
 * If cancelled, the mute status change will be prevented.
 * </p>
 * <p>
 * For global mute status changes, an independent {@link SocialUserMuteStatusChange} event
 * will be fired for each channel.
 * </p>
 * 
 * <p>Fields:</p>
 * <ul>
 *     <li><b>user:</b> The {@link SocialUser} whose mute status is changing.</li>
 *     <li><b>channel:</b> The {@link ChatChannel} where the mute status will change.</li>
 *     <li><b>status:</b> {@code true} if the user will be muted, {@code false} if the user will be unmuted.</li>
 *     <li><b>cancelled:</b> Whether this event has been cancelled, preventing the mute status change.</li>
 * </ul>
 */
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

    private final SocialUser user;

    private final ChatChannel channel;

    private final Boolean status;

    private boolean cancelled = false;

}