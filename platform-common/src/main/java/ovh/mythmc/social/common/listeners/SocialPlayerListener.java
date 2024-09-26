package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.UUID;

public final class SocialPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer == null) {
            Social.get().getPlayerManager().registerSocialPlayer(uuid);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);

        if (socialPlayer != null)
            Social.get().getPlayerManager().unregisterSocialPlayer(socialPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrivateMessage(SocialPrivateMessageEvent event) {
        if (!event.isCancelled())
            Social.get().getChatManager().sendPrivateMessage(event.getSender(), event.getRecipient(), event.getMessage());
    }

}
