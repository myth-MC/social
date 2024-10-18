package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Objects;

public final class AnvilListener implements Listener {

    @EventHandler
    public void onItemRename(PrepareAnvilEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getInventory().getViewers().get(0).getUniqueId());
        if (socialPlayer == null)
            return;

        if (event.getResult() == null || !event.getResult().hasItemMeta() || Objects.equals(event.getView().getRenameText(), ""))
            return;

        String name = event.getView().getRenameText();
        String parsedName = LegacyComponentSerializer.legacySection().serialize(
                Social.get().getTextProcessor().parsePlayerInput(socialPlayer, name)
        );

        ItemMeta resultMeta = event.getResult().getItemMeta();
        resultMeta.setDisplayName(parsedName);
        event.getResult().setItemMeta(resultMeta);
    }

}
