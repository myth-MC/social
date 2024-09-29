package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
public class SocialChatMessageReceiveEvent extends Event implements Cancellable {

    private final SocialPlayer sender;

    private final SocialPlayer recipient;

    private final ChatChannel chatChannel;

    private Component message;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialChatMessageReceiveEvent(SocialPlayer sender, SocialPlayer recipient, ChatChannel chatChannel, Component message) {
        super(true);
        this.sender = sender;
        this.recipient = recipient;
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
