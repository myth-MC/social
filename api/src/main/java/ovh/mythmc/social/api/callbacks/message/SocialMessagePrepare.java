package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
@Setter
@Accessors(fluent = true)
@Callback
public class SocialMessagePrepare {

    @CallbackFieldGetter("sender")
    private final SocialUser sender;

    @CallbackFieldGetter("channel")
    private @NotNull ChatChannel channel;

    @CallbackFieldGetter("plainMessage")
    private @NotNull String plainMessage;

    @CallbackFieldGetter("replyId")
    private Integer replyId;

    @CallbackFieldGetter("cancelled")
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
