package ovh.mythmc.social.api.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.database.reference.AbstractUUIDResolver;
import ovh.mythmc.social.api.database.reference.OfflinePlayerReference;

import java.util.Arrays;
import java.util.Objects;

public class BukkitUUIDResolver extends AbstractUUIDResolver implements Listener {

    BukkitUUIDResolver() {
    }

    @Override
    protected Iterable<OfflinePlayerReference> serverOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
            .map(offlinePlayer -> OfflinePlayerReference.of(offlinePlayer.getUniqueId(), Objects.requireNonNull(offlinePlayer.getName())))
            .toList();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.of(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final OfflinePlayer offlinePlayer = event.getPlayer();
        update(OfflinePlayerReference.of(offlinePlayer.getUniqueId(), offlinePlayer.getName()));
    }

}
