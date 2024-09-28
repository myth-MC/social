package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class SystemMessagesListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        // Send message to console
        Social.get().getLogger().info(event.getJoinMessage() + "");

        String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getJoinMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        Component message = Social.get().getTextProcessor().process(socialPlayer, unformattedMessage);
        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getPlayerManager().get(), message, channelType);

        event.setJoinMessage("");
    }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
       SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
       if (socialPlayer == null)
           return;

       // Send message to console
       Social.get().getLogger().info(event.getQuitMessage());

       String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getQuitMessage();
       if (unformattedMessage == null || unformattedMessage.isEmpty())
           return;

       Component message = Social.get().getTextProcessor().process(socialPlayer, unformattedMessage);
       ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

       Social.get().getTextProcessor().send(Social.get().getPlayerManager().get(), message, channelType);

       event.setQuitMessage("");
   }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getEntity().getUniqueId());
        if (socialPlayer == null)
            return;

        String unformattedMessage = Social.get().getConfig().getSettings().getSystemMessages().getDeathMessage();
        if (unformattedMessage == null || unformattedMessage.isEmpty())
            return;

        String deathMessage = String.format(unformattedMessage, event.getDeathMessage());
        deathMessage = deathMessage.replace(event.getEntity().getName(),
                Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat());

        Component message = Social.get().getTextProcessor().process(socialPlayer, deathMessage);
        ChannelType channelType = ChannelType.valueOf(Social.get().getConfig().getSettings().getSystemMessages().getChannelType());

        Social.get().getTextProcessor().send(Social.get().getPlayerManager().get(), message, channelType);

        // Send message to console
        Social.get().getLogger().info(event.getDeathMessage());

        event.setDeathMessage("");
   }

}
