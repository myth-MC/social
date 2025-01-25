package ovh.mythmc.social.api.events.users;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ovh.mythmc.social.api.database.model.IgnoredUser.IgnoreScope;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
public class SocialUserIgnoreEvent extends Event implements Cancellable {

    private final SocialUser user;

    private final SocialUser target;

    private final IgnoreScope scope;

    private static final @NotNull HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
