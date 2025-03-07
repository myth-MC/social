package ovh.mythmc.social.api.callback.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFields({
    @CallbackField(field = "sender", getter = "sender()"),
    @CallbackField(field = "channel", getter = "channel()"),
    @CallbackField(field = "plainMessage", getter = "plainMessage()"),
    @CallbackField(field = "replyId", getter = "replyId()"),
    @CallbackField(field = "cancelled", getter = "cancelled()", isExtraParameter = true)
})
public class SocialMessagePrepare {

    private final AbstractSocialUser sender;

    private @NotNull ChatChannel channel;

    private @NotNull String plainMessage;

    private Integer replyId;

    private boolean cancelled = false;

    public SocialMessagePrepare(AbstractSocialUser sender, ChatChannel channel, String plainMessage, Integer replyId) {
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
