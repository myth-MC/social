package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import ovh.mythmc.social.api.users.SocialUser;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SocialPrivateMessageEvent extends Event implements Cancellable {

    private final SocialUser sender;

    private final SocialUser recipient;

    private String message;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialPrivateMessageEvent(SocialUser sender, SocialUser recipient, String message) {
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
