package ovh.mythmc.social.bukkit.callback.game.invoker;

import java.util.Objects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.AnvilRename;
import ovh.mythmc.social.common.callback.game.AnvilRenameCallback;

public class AnvilRenameInvoker implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        final var user = BukkitSocialUser.from(event.getInventory().getViewers().getFirst().getUniqueId());
        if (user == null)
            return;

        if (event.getResult() == null || !event.getResult().hasItemMeta() || Objects.equals(event.getView().getRenameText(), ""))
            return;

        final Component name = Component.text(event.getView().getRenameText());

        AnvilRenameCallback.INSTANCE.invoke(new AnvilRename(user, name), result -> {
            final ItemMeta resultMeta = event.getResult().getItemMeta();
            final String resultName = PlainTextComponentSerializer.plainText().serialize(result.name());

            resultMeta.setDisplayName(resultName);
            event.getResult().setItemMeta(resultMeta);
        });
    }
    
}
