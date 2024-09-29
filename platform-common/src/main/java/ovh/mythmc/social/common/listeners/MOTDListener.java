package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class MOTDListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialPlayer player = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (player == null)
            return;

        Social.get().getConfig().getSettings().getMotd().getMessage().forEach(line -> Social.get().getTextProcessor().parseAndSend(player, line, ChannelType.CHAT));
    }

}
