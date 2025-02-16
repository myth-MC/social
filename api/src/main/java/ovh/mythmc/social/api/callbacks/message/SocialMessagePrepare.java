package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "sender", getter = "sender()"),
    @CallbackFieldGetter(field = "channel", getter = "channel()"),
    @CallbackFieldGetter(field = "plainMessage", getter = "plainMessage()"),
    @CallbackFieldGetter(field = "replyId", getter = "replyId()"),
    @CallbackFieldGetter(field = "cancelled", getter = "cancelled()")
})
public class SocialMessagePrepare {

    private final SocialUser sender;

    private @NotNull ChatChannel channel;

    private @NotNull String plainMessage;

    private Integer replyId;

    private boolean cancelled = false;

    public SocialMessagePrepare(SocialUser sender, ChatChannel channel, String plainMessage, Integer replyId) {
        this.sender = sender;
        this.channel = channel;
        this.plainMessage = plainMessage;
        this.replyId = replyId;
    }

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
