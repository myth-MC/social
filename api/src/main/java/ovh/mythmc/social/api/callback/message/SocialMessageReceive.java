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
 *
 * @param sender     the {@link SocialUser} who sent the original message
 * @param recipient  the {@link SocialUser} who will receive the message
 * @param channel    the {@link ChatChannel} the message was sent in
 * @param message    the {@link Component} representing the message content
 * @param messageId  the unique identifier of the message
 * @param replyId    the identifier of the message being replied to, or {@code null} if this is not a reply
 * @param cancelled  whether this event has been cancelled, preventing the message from being delivered
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

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

}
