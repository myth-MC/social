package ovh.mythmc.social.api.callbacks.reaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "user", getter = "user()"),
    @CallbackFieldGetter(field = "reaction", getter = "reaction()"),
    @CallbackFieldGetter(field = "cancelled", getter = "cancelled()")
})
public final class SocialReactionTrigger {

    private final SocialUser user;
    
    private final Reaction reaction;

    private boolean cancelled = false;
    
}
