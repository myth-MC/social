package ovh.mythmc.social.api.callbacks.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetter;
import ovh.mythmc.callbacks.annotations.v1.CallbackFieldGetters;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
@CallbackFieldGetters({
    @CallbackFieldGetter(field = "sender", getter = "sender()"),
    @CallbackFieldGetter(field = "channel", getter = "channel()"),
    @CallbackFieldGetter(field = "message", getter = "message()"),
    @CallbackFieldGetter(field = "messageId", getter = "messageId()"),
    @CallbackFieldGetter(field = "replyId", getter = "replyId()")
})
public final class SocialMessageSend {

    private final SocialUser sender;

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
