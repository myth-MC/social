package ovh.mythmc.social.api.events.reactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.users.SocialUser;

@RequiredArgsConstructor
@Getter
@Setter
public class SocialReactionCallEvent extends Event implements Cancellable {

    private final SocialUser user;

    private final Reaction reaction;

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
