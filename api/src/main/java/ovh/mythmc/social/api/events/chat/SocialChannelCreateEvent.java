package ovh.mythmc.social.api.events.chat;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ovh.mythmc.social.api.chat.ChatChannel;

@Getter
@Setter
@RequiredArgsConstructor
public class SocialChannelCreateEvent extends Event {

    private final ChatChannel channel;

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
