package ovh.mythmc.social.paper.callback.game.invoker;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.SignEdit;
import ovh.mythmc.social.common.callback.game.SignEditCallback;

public class SignEditInvoker implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null)
            return;

        final List<Component> lines = event.lines();

        SignEditCallback.INSTANCE.invoke(new SignEdit(user, lines), result -> {
            for (int i = 0; i < result.lines().size(); i++) {
                final Component line = result.lines().get(i);

                event.line(i, line);
            }
        });
    }
    
}
