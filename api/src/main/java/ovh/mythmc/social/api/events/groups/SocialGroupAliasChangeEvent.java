package ovh.mythmc.social.api.events.groups;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ovh.mythmc.social.api.chat.GroupChatChannel;

@Getter
@Setter
@AllArgsConstructor
public class SocialGroupAliasChangeEvent extends Event {

    private final GroupChatChannel groupChatChannel;

    private String alias;

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
