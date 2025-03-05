package ovh.mythmc.social.bukkit.callback.game.invoker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.UserChat;
import ovh.mythmc.social.common.callback.game.UserChatCallback;

public class UserChatInvoker implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        UserChatCallback.INSTANCE.invoke(new UserChat(user, event.getMessage()), result -> {
            event.setMessage(result.message());
            event.setCancelled(result.cancelled());
        });
    }
    
}
