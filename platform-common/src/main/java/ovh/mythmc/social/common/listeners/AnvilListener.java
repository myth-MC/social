package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.users.SocialUser;

import static net.kyori.adventure.text.Component.text;

import java.util.Objects;

public final class AnvilListener implements Listener {

    @EventHandler
    public void onItemRename(PrepareAnvilEvent event) {
        SocialUser socialPlayer = Social.get().getPlayerManager().get(event.getInventory().getViewers().get(0).getUniqueId());
        if (socialPlayer == null)
            return;

        if (event.getResult() == null || !event.getResult().hasItemMeta() || Objects.equals(event.getView().getRenameText(), ""))
            return;

        String name = event.getView().getRenameText();

        String parsedName = PlainTextComponentSerializer.plainText().serialize(
            Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder()
                .user(socialPlayer)
                .channel(socialPlayer.getMainChannel())
                .message(text(name))
                .build())
        );

        ItemMeta resultMeta = event.getResult().getItemMeta();
        resultMeta.setDisplayName(parsedName);
        event.getResult().setItemMeta(resultMeta);
    }

}
