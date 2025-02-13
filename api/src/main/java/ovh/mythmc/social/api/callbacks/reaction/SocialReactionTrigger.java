package ovh.mythmc.social.api.callbacks.reaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialReactionTrigger {

    @CallbackFieldGetter("user")
    private final SocialUser user;
    
    @CallbackFieldGetter("reaction")
    private final Reaction reaction;

    @CallbackFieldGetter("cancelled")
    private boolean cancelled = false;
    
}
