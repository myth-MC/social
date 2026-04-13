package ovh.mythmc.social.api.callback.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public SocialMessageReceive(
        @NotNull SocialUser sender,
        @NotNull SocialUser recipient,
        @NotNull ChatChannel channel,
        @NotNull Component message,
        int messageId,
        @Nullable Integer replyId
    ) {
        this.sender = sender;
        this.recipient = recipient;
        this.channel = channel;
        this.message = message;
        this.messageId = messageId;
        this.replyId = replyId;
    }

    public @NotNull SocialUser sender() {
        return this.sender;
    }

    public @NotNull SocialUser recipient() {
        return this.recipient;
    }

    public @NotNull ChatChannel channel() {
        return this.channel;
    }

    public @NotNull Component message() {
        return this.message;
    }

    public int messageId() {
        return this.messageId;
    }

    public @Nullable Integer replyId() {
        return this.replyId;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void message(@NotNull Component message) {
        this.message = message;
    }

    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

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