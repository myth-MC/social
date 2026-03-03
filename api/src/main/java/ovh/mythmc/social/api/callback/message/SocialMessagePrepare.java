package ovh.mythmc.social.api.callback.message;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.Set;

/**
 * Event fired before a message is sent, allowing modification or cancellation.
 *
 * @param sender       the {@link SocialUser} who sent the original message
 * @param channel      the {@link ChatChannel} the message will be sent to
 * @param viewers      the {@link Set} of {@link Audience Audiences} that will receive the message
 * @param plainMessage the original, unformatted {@link String} message content
 * @param replyId      the identifier of the message being replied to, or {@code null} if this is not a reply
 * @param cancelled    whether this event has been cancelled, preventing the message from being sent
 */
@Getter
@Setter
@Accessors(fluent = true)
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

    public SocialMessagePrepare(SocialUser sender, ChatChannel channel, Set<Audience> viewers, String plainMessage,
            Integer replyId) {
        this.sender = sender;
        this.channel = channel;
        this.viewers = viewers;
        this.plainMessage = plainMessage;
        this.replyId = replyId;
    }

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

}
