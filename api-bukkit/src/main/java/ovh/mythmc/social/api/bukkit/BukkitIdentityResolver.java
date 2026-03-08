package ovh.mythmc.social.api.bukkit;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.identity.AbstractIdentityResolver;
import ovh.mythmc.social.api.identity.Identified;
import ovh.mythmc.social.api.identity.SimpleIdentity;

public class BukkitIdentityResolver extends AbstractIdentityResolver implements Listener {

    @Override
    protected Iterable<Identified> serverIdentities() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(BukkitIdentityResolver::createIdentity)
                .toList();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        updateOrCreate(createIdentity(event.getPlayer()));
    }

    private static Identified createIdentity(@NotNull OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        String username = offlinePlayer.getName();
        return new SimpleIdentity(uuid, username);
    }

}
