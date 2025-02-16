package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "sender", getter = "sender()"),
    @CallbackFieldGetter(field = "recipient", getter = "recipient()"),
    @CallbackFieldGetter(field = "channel", getter = "channel()"),
    @CallbackFieldGetter(field = "message", getter = "message()"),
    @CallbackFieldGetter(field = "messageId", getter = "messageId()"),
    @CallbackFieldGetter(field = "replyId", getter = "replyId()"),
    @CallbackFieldGetter(field = "cancelled", getter = "cancelled()")
})
public class SocialMessageReceive {

    private final SocialUser sender;

    private final SocialUser recipient;

    private final ChatChannel channel;

    private @NotNull Component message;

    private final int messageId;

    private final Integer replyId;

    private boolean cancelled = false;

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
