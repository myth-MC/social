package ovh.mythmc.social.paper.callback.game.invoker;

import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.UserPresence;
import ovh.mythmc.social.common.callback.game.UserPresenceCallback;

public class UserPresenceInvoker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null) {
            user = BukkitSocialUser.from(Social.get().getUserService().register(event.getPlayer().getUniqueId()));
            System.out.println("crear");
        }

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.ofNullable(user), UserPresence.Type.LOGIN, Optional.empty()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        final Component message = event.joinMessage();

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.ofNullable(user), UserPresence.Type.JOIN, Optional.ofNullable(message)), result -> {
            result.message().ifPresentOrElse(joinMessage -> {
                event.joinMessage(joinMessage);
            }, () -> event.joinMessage(null));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        final Component message = event.quitMessage();

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.ofNullable(user), UserPresence.Type.QUIT, Optional.ofNullable(message)), result -> {
            result.message().ifPresentOrElse(quitMessage -> {
                event.quitMessage(quitMessage);
            }, () -> event.quitMessage(null));
        });
    }
}
