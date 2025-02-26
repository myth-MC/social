package ovh.mythmc.social.api.callback.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.user.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "sender", getter = "sender()"),
    @CallbackField(field = "recipient", getter = "recipient()"),
    @CallbackField(field = "plainMessage", getter = "plainMessage()"),
    @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class SocialPrivateMessageSend {

    private final SocialUser sender;

    private final SocialUser recipient;

    private @NotNull String plainMessage;

    private boolean cancelled = false;

}