package ovh.mythmc.social.api.callback.message;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.Set;

/**
 * Event fired before a message is sent, allowing modification or cancellation.
 * <p>
 * This event provides details about the message, the sender, the channel, and the viewers. It allows for
 * modifications or cancellation of the message before it is sent.
 * </p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>sender:</b> The {@link SocialUser} who sent the original message.</li>
 *     <li><b>channel:</b> The {@link ChatChannel} the message will be sent to.</li>
 *     <li><b>viewers:</b> A {@link Set} of {@link Audience} who will receive the message.</li>
 *     <li><b>plainMessage:</b> The original, unformatted {@link String} message content.</li>
 *     <li><b>replyId:</b> The identifier of the message being replied to, or {@code null} if this is not a reply.</li>
 *     <li><b>cancelled:</b> Whether this event has been cancelled, preventing the message from being sent.</li>
 * </ul>
 */
@Callback
@CallbackFields({
        @CallbackField(field = "sender", getter = "sender()"),
        @CallbackField(field = "channel", getter = "channel()"),
        @CallbackField(field = "viewers", getter = "viewers()"),
        @CallbackField(field = "plainMessage", getter = "plainMessage()"),
        @CallbackField(field = "replyId", getter = "replyId()"),
        @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public final class SocialMessagePrepare {

    private final SocialUser sender;
    private @NotNull ChatChannel channel;
    private final Set<Audience> viewers;
    private @NotNull String plainMessage;
    private Integer replyId;
    private boolean cancelled = false;

    /**
     * Constructs a new {@code SocialMessagePrepare} event with the given sender, channel, viewers, message,
     * and optional reply ID.
     * 
     * @param sender the {@link SocialUser} who is sending the message
     * @param channel the {@link ChatChannel} the message will be sent to
     * @param viewers the {@link Set} of {@link Audience} that will receive the message
     * @param plainMessage the original unformatted message content
     * @param replyId the ID of the message being replied to, or {@code null} if there is no reply
     */
    public SocialMessagePrepare(SocialUser sender, ChatChannel channel, Set<Audience> viewers, String plainMessage,
            Integer replyId) {
        this.sender = sender;
        this.channel = channel;
        this.viewers = viewers;
        this.plainMessage = plainMessage;
        this.replyId = replyId;
    }

    public @NotNull SocialUser sender() {
        return this.sender;
    }

    public @NotNull ChatChannel channel() {
        return this.channel;
    }

    public @NotNull Set<Audience> viewers() {
        return this.viewers;
    }

    public @NotNull String plainMessage() {
        return this.plainMessage;
    }

    public @Nullable Integer replyId() {
        return this.replyId;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void channel(@NotNull ChatChannel channel) {
        this.channel = channel;
    }

    public void replyId(@Nullable Integer replyId) {
        this.replyId = replyId;
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