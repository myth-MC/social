package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.UUID;

public final class SocialPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer == null) {
            Social.get().getPlayerManager().registerSocialPlayer(uuid);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer != null)
            Social.get().getPlayerManager().unregisterSocialPlayer(socialPlayer);
    }

}
