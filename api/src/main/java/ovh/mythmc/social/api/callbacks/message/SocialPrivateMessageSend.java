package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "sender", getter = "sender()"),
    @CallbackFieldGetter(field = "recipient", getter = "recipient()"),
    @CallbackFieldGetter(field = "plainMessage", getter = "plainMessage()"),
    @CallbackFieldGetter(field = "cancelled", getter = "cancelled()")
})
public final class SocialPrivateMessageSend {

    private final SocialUser sender;

    private final SocialUser recipient;

    private @NotNull String plainMessage;

    private boolean cancelled = false;

}