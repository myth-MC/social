package ovh.mythmc.social.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.bukkit.SocialBukkit;
import java.util.UUID;

public final class SocialUserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        final var user = BukkitSocialUser.from(event.getPlayer());

        if (user == null)
            SocialBukkit.get().getUserService().register(event.getPlayer(), uuid);
    }

}
