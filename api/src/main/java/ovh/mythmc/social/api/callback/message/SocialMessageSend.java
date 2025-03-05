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
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

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
    @CallbackField(field = "replyId", getter = "replyId()")
})
public final class SocialMessageSend {

    private final AbstractSocialUser<? extends Object> sender;

    private final ChatChannel channel;

    private final Component message;

    private final int messageId;

    private final Integer replyId;

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
