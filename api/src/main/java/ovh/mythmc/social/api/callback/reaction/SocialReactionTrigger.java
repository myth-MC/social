package ovh.mythmc.social.api.callback.reaction;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired before a {@link Reaction} is displayed.
 * <p>
 * This event is triggered before a reaction is displayed in the system. It allows for modification or 
 * cancellation of the reaction display. If the event is cancelled, the reaction will not be displayed.
 * </p>
 * 
 * <p>Fields:</p>
 * <ul>
 *     <li><b>user:</b> The {@link SocialUser} who triggered the reaction.</li>
 *     <li><b>reaction:</b> The {@link Reaction} being triggered.</li>
 *     <li><b>cancelled:</b> Whether this event has been cancelled, preventing the reaction from being displayed.</li>
 * </ul>
 */
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

    public SocialReactionTrigger(
        @NotNull SocialUser user,
        @NotNull Reaction reaction
    ) {
        this.user = user;
        this.reaction = reaction;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull Reaction reaction() {
        return this.reaction;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}