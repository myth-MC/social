package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
public class SocialChatMessageSendEvent extends Event implements Cancellable {

    private final SocialPlayer sender;

    private final ChatChannel chatChannel;

    private String message;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialChatMessageSendEvent(SocialPlayer sender, ChatChannel chatChannel, String message) {
        super(true);
        this.sender = sender;
        this.chatChannel = chatChannel;
        this.message = message;
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
