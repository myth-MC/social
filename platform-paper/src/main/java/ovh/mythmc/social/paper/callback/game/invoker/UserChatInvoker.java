package ovh.mythmc.social.paper.callback.game.invoker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.UserChat;
import ovh.mythmc.social.common.callback.game.UserChatCallback;

public class UserChatInvoker implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        UserChatCallback.INSTANCE.invoke(new UserChat(user, (TextComponent) event.message()), result -> {
            event.message(result.message());
            if (result.cancelled())
                event.setCancelled(true);
        });
    }
    
}
