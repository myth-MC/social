package ovh.mythmc.social.common.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.UUID;

public final class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        UUID uuid = event.getPlayer().getUniqueId();
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(uuid);
        if (socialPlayer == null)
            return;

        if (socialPlayer.isMuted()) {
            event.setCancelled(true);
            return;
        }

        ChatChannel mainChannel = socialPlayer.getMainChannel();
        if (mainChannel == null) {
            // error: main channel doesn't exist
            return;
        }

        if (mainChannel.isPassthrough())
            return;

        event.setCancelled(true);
        Social.get().getChatManager().sendChatMessage(socialPlayer, mainChannel, event.getMessage());
    }

}
