package ovh.mythmc.social.api.events.groups;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
@RequiredArgsConstructor
public class SocialGroupInviteEvent extends Event {

    private final GroupChatChannel groupChatChannel;

    private final SocialPlayer player;

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
