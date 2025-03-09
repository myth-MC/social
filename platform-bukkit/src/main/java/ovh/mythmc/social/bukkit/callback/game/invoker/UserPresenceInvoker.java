package ovh.mythmc.social.bukkit.callback.game.invoker;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
        }

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.of(user), UserPresence.Type.LOGIN, Optional.empty()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        final var message = Component.text(ChatColor.stripColor(event.getJoinMessage()));

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.of(user), UserPresence.Type.JOIN, Optional.of(message)), result -> {
            result.message().ifPresentOrElse(joinMessage -> {
                event.setJoinMessage(LegacyComponentSerializer.legacySection().serialize(joinMessage));
            }, () -> event.setJoinMessage(null));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        final var message = Component.text(ChatColor.stripColor(event.getQuitMessage()));

        UserPresenceCallback.INSTANCE.invoke(new UserPresence(Optional.of(user), UserPresence.Type.QUIT, Optional.of(message)), result -> {
            result.message().ifPresentOrElse(quitMessage -> {
                event.setQuitMessage(LegacyComponentSerializer.legacySection().serialize(quitMessage));
            }, () -> event.setQuitMessage(null));
        });
    }
    
}
