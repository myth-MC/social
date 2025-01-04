package ovh.mythmc.social.common.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.chat.SocialPrivateMessageEvent;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.UUID;

public final class SocialUserListener implements Listener {

    // Temporary workaround for nicknames
    NamespacedKey key = new NamespacedKey("social", "nickname");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SocialUser user = Social.get().getUserManager().get(uuid);

        if (user == null)
            Social.get().getUserManager().register(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().get(event.getPlayer().getUniqueId());

        // Temporary workaround for nicknames
        PersistentDataContainer container = user.getPlayer().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            String nickname = container.get(key, PersistentDataType.STRING);
            user.getPlayer().setDisplayName(nickname);
            user.getNickname(); // Updates cached nickname
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();

        container.set(key, PersistentDataType.STRING, event.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrivateMessage(SocialPrivateMessageEvent event) {
        if (!event.isCancelled())
            Social.get().getChatManager().sendPrivateMessage(event.getSender(), event.getRecipient(), event.getMessage());
    }

}
