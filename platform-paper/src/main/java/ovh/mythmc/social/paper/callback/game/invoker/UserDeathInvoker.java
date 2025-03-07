package ovh.mythmc.social.paper.callback.game.invoker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.UserDeath;
import ovh.mythmc.social.common.callback.game.UserDeathCallback;

public class UserDeathInvoker implements Listener {
   
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final var user = BukkitSocialUser.from(event.getEntity().getUniqueId());
        if (user == null)
            return;

        UserDeathCallback.INSTANCE.invoke(new UserDeath(user, event.deathMessage()), result -> {
            final Component deathMessage = result.deathMessage();
            event.deathMessage(deathMessage);
        });
    }
    
}
