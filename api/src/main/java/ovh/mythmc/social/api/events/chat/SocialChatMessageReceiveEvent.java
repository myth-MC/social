package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
@Setter
public class SocialChatMessageReceiveEvent extends SocialChatMessagePrepareEvent {

    private final SocialUser recipient;

    private Component message;

    private final int messageId;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialChatMessageReceiveEvent(SocialUser sender, SocialUser recipient, ChatChannel chatChannel, Component message, String rawMessage, Integer replyId, int messageId) {
        super(sender, chatChannel, rawMessage, replyId);
        this.recipient = recipient;
        this.message = message;
        this.messageId = messageId;
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
