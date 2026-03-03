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
 * <p>
 * This event allows you to perform actions before the message is actually delivered. It provides
 * details about the sender, the channel, the message content, and whether the message is a reply
 * to another message. If the event is cancelled, the message will not be sent.
 * </p>
 * <p>
 * For the event fired while preparing a mutable message, see {@link SocialMessagePrepare}.
 * </p>
 * 
 * <p>Fields:</p>
 * <ul>
 *     <li><b>sender:</b> The {@link SocialUser} who sent the original message.</li>
 *     <li><b>channel:</b> The {@link ChatChannel} the message will be sent in.</li>
 *     <li><b>message:</b> The {@link Component} representing the message content.</li>
 *     <li><b>messageId:</b> The unique identifier of the message.</li>
 *     <li><b>replyId:</b> The identifier of the message being replied to, or {@code null} if this is not a reply.</li>
 *     <li><b>cancelled:</b> Whether this event has been cancelled, preventing the message from being sent.</li>
 * </ul>
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

    /**
     * Determines if the current message is a reply to another message.
     * 
     * @return {@code true} if this message is a reply, otherwise {@code false}.
     */
    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

}