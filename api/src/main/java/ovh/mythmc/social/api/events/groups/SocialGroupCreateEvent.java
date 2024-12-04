package ovh.mythmc.social.api.events.groups;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ovh.mythmc.social.api.channels.GroupChatChannel;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class SocialGroupCreateEvent extends Event {

    private final GroupChatChannel groupChatChannel;

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
