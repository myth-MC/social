package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.users.SocialUser;

public final class MOTDListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser player = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
        if (player == null)
            return;

        Social.get().getConfig().getSettings().getMotd().getMessage().forEach(line -> Social.get().getTextProcessor().parseAndSend(player, player.getMainChannel(), line, ChannelType.CHAT));
    }

}
