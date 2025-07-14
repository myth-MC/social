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
import ovh.mythmc.social.api.user.AbstractSocialUser;

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

    private final AbstractSocialUser sender;

    private final AbstractSocialUser recipient;

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
