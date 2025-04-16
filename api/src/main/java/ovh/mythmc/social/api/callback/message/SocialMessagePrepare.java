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
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.Set;

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

    private final AbstractSocialUser sender;

    private @NotNull ChatChannel channel;

    private final Set<Audience> viewers;

    private @NotNull String plainMessage;

    private Integer replyId;

    private boolean cancelled = false;

    public SocialMessagePrepare(AbstractSocialUser sender, ChatChannel channel, Set<Audience> viewers, String plainMessage, Integer replyId) {
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
