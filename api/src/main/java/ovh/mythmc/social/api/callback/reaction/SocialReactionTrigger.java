package ovh.mythmc.social.api.callback.reaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired before a {@link Reaction} is displayed.
 *
 * @param user      the {@link SocialUser} who triggered the reaction
 * @param reaction  the {@link Reaction} being triggered
 * @param cancelled whether this event has been cancelled, preventing the reaction from being displayed
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "reaction", getter = "reaction()"),
        @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class SocialReactionTrigger {

    private final SocialUser user;

    private final Reaction reaction;

    private boolean cancelled = false;

}
