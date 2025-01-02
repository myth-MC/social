package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.Objects;

@Getter
@Setter
public class SocialChatMessagePrepareEvent extends Event implements Cancellable {

    private final SocialUser sender;

    private ChatChannel chatChannel;

    private String rawMessage;

    private final Integer replyId;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialChatMessagePrepareEvent(SocialUser sender, ChatChannel chatChannel, String message, Integer replyId) {
        super(true);
        this.sender = sender;
        this.chatChannel = chatChannel;
        this.rawMessage = message;
        this.replyId = replyId;
    }

    public boolean isReply() {
        if (Objects.equals(getReplyId(), null))
            return false;

        return Social.get().getChatManager().getHistory().getById(getReplyId()) != null;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
