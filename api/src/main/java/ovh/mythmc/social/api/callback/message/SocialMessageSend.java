package ovh.mythmc.social.api.callback.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Event fired before a finalized (immutable) message is sent.
 *
 * <p>For the event fired while preparing a mutable message, see
 * {@link SocialMessagePrepare}.
 *
 * @param sender     the {@link SocialUser} who sent the original message
 * @param channel    the {@link ChatChannel} the message will be sent in
 * @param message    the {@link Component} representing the message content
 * @param messageId  the unique identifier of the message
 * @param replyId    the identifier of the message being replied to, or {@code null} if this is not a reply
 * @param cancelled  whether this event has been cancelled, preventing the message from being sent
 */
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
        @CallbackField(field = "sender", getter = "sender()"),
        @CallbackField(field = "channel", getter = "channel()"),
        @CallbackField(field = "message", getter = "message()"),
        @CallbackField(field = "messageId", getter = "messageId()"),
        @CallbackField(field = "replyId", getter = "replyId()"),
        @CallbackField(field = "cancelled", getter = "cancelled()")
})
public final class SocialMessageSend {

    private final SocialUser sender;

    private final ChatChannel channel;

    private final Component message;

    private final int messageId;

    private final Integer replyId;

    private final boolean cancelled;

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

}
