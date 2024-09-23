package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class SystemAnnouncementsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        event.setJoinMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            String unformattedMessage = Social.get().getSettings().get().getSystemMessages().getJoinMessage();
            Component message = Social.get().getPlaceholderProcessor().process(socialPlayer, unformattedMessage);
            SocialAdventureProvider.get().sendMessage(player, message);
        }
    }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
       SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
       if (socialPlayer == null)
           return;

       event.setQuitMessage("");
       for (Player player : Bukkit.getOnlinePlayers()) {
           String unformattedMessage = Social.get().getSettings().get().getSystemMessages().getQuitMessage();
           Component message = Social.get().getPlaceholderProcessor().process(socialPlayer, unformattedMessage);
           SocialAdventureProvider.get().sendMessage(player, message);
       }
   }

}
