package ovh.mythmc.social.api.callbacks.message;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.gestalt.callbacks.v1.annotations.Callback;
import ovh.mythmc.gestalt.callbacks.v1.annotations.CallbackFieldGetter;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Callback
public class SocialMessageReceive {

    @CallbackFieldGetter("sender")
    private final SocialUser sender;

    @CallbackFieldGetter("recipient")
    private final SocialUser recipient;

    @CallbackFieldGetter("channel")
    private final ChatChannel channel;

    @CallbackFieldGetter("message")
    private @NotNull Component message;

    @CallbackFieldGetter("messageId")
    private final int messageId;

    @CallbackFieldGetter("replyId")
    private final Integer replyId;

    @CallbackFieldGetter("cancelled")
    private boolean cancelled = false;

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
