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
public class SocialPlayerChatMessageEvent extends Event implements Cancellable {

    private final SocialPlayer socialPlayer;

    private final ChatChannel chatChannel;

    private final String message;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public SocialPlayerChatMessageEvent(SocialPlayer socialPlayer, ChatChannel chatChannel, String message) {
        super(true);
        this.socialPlayer = socialPlayer;
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
