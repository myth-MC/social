package ovh.mythmc.social.common.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.user.SocialUser;

public final class MOTDListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        Social.get().getConfig().getMotd().getMessage().forEach(line -> Social.get().getTextProcessor().parseAndSend(user, user.getMainChannel(), line, ChannelType.CHAT));
    }

}
