package ovh.mythmc.social.bukkit.callback.game.invoker;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.UserDeath;
import ovh.mythmc.social.common.callback.game.UserDeathCallback;

public class UserDeathInvoker implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final var user = BukkitSocialUser.from(event.getEntity().getUniqueId());
        if (user == null)
            return;

        UserDeathCallback.INSTANCE.invoke(new UserDeath(user, Component.text(ChatColor.stripColor(event.getDeathMessage()))), result -> {
            final String deathMessage = LegacyComponentSerializer.legacySection().serialize(result.deathMessage());
            event.setDeathMessage(deathMessage);
        });
    }
    
}
