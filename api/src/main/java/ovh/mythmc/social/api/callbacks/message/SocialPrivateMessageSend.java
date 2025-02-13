package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialPrivateMessageSend {

    @CallbackFieldGetter("sender")
    private final SocialUser sender;

    @CallbackFieldGetter("recipient")
    private final SocialUser recipient;

    @CallbackFieldGetter("plainMessage")
    private @NotNull String plainMessage;

    @CallbackFieldGetter("cancelled")
    private boolean cancelled = false;

}