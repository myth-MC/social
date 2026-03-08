package ovh.mythmc.social.api.callback.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
 * Event fired before a message is delivered to a specific {@link SocialUser}.
 * <p>
 * This event allows modification or cancellation of the message before it is delivered to the recipient.
 * It provides details about the message, the sender, the recipient, and the channel, as well as
 * whether the message is a reply to another message.
 * </p>
 * 
 * <p>Fields:</p>
 * <ul>
 *     <li><b>sender:</b> The {@link SocialUser} who sent the original message.</li>
 *     <li><b>recipient:</b> The {@link SocialUser} who will receive the message.</li>
 *     <li><b>channel:</b> The {@link ChatChannel} the message was sent in.</li>
 *     <li><b>message:</b> The {@link Component} representing the message content.</li>
 *     <li><b>messageId:</b> The unique identifier of the message.</li>
 *     <li><b>replyId:</b> The identifier of the message being replied to, or {@code null} if this is not a reply.</li>
 *     <li><b>cancelled:</b> Whether this event has been cancelled, preventing the message from being delivered.</li>
 * </ul>
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
        @CallbackField(field = "sender", getter = "sender()"),
        @CallbackField(field = "recipient", getter = "recipient()"),
        @CallbackField(field = "channel", getter = "channel()"),
        @CallbackField(field = "message", getter = "message()"),
        @CallbackField(field = "messageId", getter = "messageId()"),
        @CallbackField(field = "replyId", getter = "replyId()"),
        @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public class SocialMessageReceive {

    private final SocialUser sender;

    private final SocialUser recipient;

    private final ChatChannel channel;

    private @NotNull Component message;

    private final int messageId;

    private final Integer replyId;

    private boolean cancelled = false;

    /**
     * Determines if the current message is a reply to another message.
     * 
     * @return {@code true} if this message is a reply, otherwise {@code false}
     */
    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

}