package ovh.mythmc.social.api.callbacks.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public final class SocialMessageSend {

    @CallbackFieldGetter("sender")
    private final SocialUser sender;

    @CallbackFieldGetter("channel")
    private final ChatChannel channel;

    @CallbackFieldGetter("message")
    private final Component message;

    @CallbackFieldGetter("messageId")
    private final int messageId;

    @CallbackFieldGetter("replyId")
    private final Integer replyId;

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
