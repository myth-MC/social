package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
public class SocialPrivateMessageEvent extends Event implements Cancellable {

    private final SocialPlayer sender;

    private final SocialPlayer recipient;

    private String message;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialPrivateMessageEvent(SocialPlayer sender, SocialPlayer recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
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
