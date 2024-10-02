package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
@RequiredArgsConstructor
public class SocialChannelSwitchEvent extends Event implements Cancellable {

    private final SocialPlayer socialPlayer;

    private final ChatChannel previousChannel;

    private final ChatChannel chatChannel;

    private boolean cancelled = false;

    private static final @NotNull HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
