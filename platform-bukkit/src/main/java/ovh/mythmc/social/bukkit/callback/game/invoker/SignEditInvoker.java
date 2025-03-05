package ovh.mythmc.social.bukkit.callback.game.invoker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.SignEdit;
import ovh.mythmc.social.common.callback.game.SignEditCallback;

public class SignEditInvoker implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null)
            return;

        final List<Component> lines = Arrays.asList(event.getLines()).stream()
            .map(line -> Component.text(line))
            .collect(Collectors.toList());

        SignEditCallback.INSTANCE.invoke(new SignEdit(user, lines), result -> {
            for (int i = 0; i < result.lines().size(); i++) {
                final String line = LegacyComponentSerializer.legacySection()
                    .serialize(result.lines().get(i));

                event.setLine(i, line);
            }
        });
    }
    
}
