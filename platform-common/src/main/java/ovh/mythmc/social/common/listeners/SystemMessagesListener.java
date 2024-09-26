package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class SystemMessagesListener implements Listener {

    ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        // Send message to console
        Social.get().getLogger().info(event.getJoinMessage() + "");

        event.setJoinMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getJoinMessage();
            if (unformattedMessage == null || unformattedMessage.isEmpty())
                return;

            Component message = Social.get().getTextProcessor().process(socialPlayer, unformattedMessage);
            SocialAdventureProvider.get().sendMessage(player, message, channelType);
        }
    }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
       SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
       if (socialPlayer == null)
           return;

       // Send message to console
       Social.get().getLogger().info(event.getQuitMessage());

       event.setQuitMessage("");
       for (Player player : Bukkit.getOnlinePlayers()) {
           String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getQuitMessage();
           if (unformattedMessage == null || unformattedMessage.isEmpty())
               return;

           Component message = Social.get().getTextProcessor().process(socialPlayer, unformattedMessage);
           SocialAdventureProvider.get().sendMessage(player, message, channelType);
       }
   }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getEntity().getUniqueId());
        if (socialPlayer == null)
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getDeathMessage();
            if (unformattedMessage == null || unformattedMessage.isEmpty())
                return;

            String deathMessage = String.format(unformattedMessage, event.getDeathMessage());
            Component message = Social.get().getTextProcessor().process(socialPlayer, deathMessage);
            SocialAdventureProvider.get().sendMessage(player, message, channelType);
        }

        // Send message to console
        Social.get().getLogger().info(event.getDeathMessage());

        event.setDeathMessage("");
   }

}
