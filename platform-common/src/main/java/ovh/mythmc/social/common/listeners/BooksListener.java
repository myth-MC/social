package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import static net.kyori.adventure.text.Component.text;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.players.SocialPlayer;

public final class BooksListener implements Listener {

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        BookMeta bookMeta = event.getNewBookMeta();

        for (int i = 1; i <= bookMeta.getPageCount(); i++) {
            String page = bookMeta.getPage(i);
            String parsedPage = LegacyComponentSerializer.legacySection().serialize(
                    Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder().socialPlayer(socialPlayer).playerChannel(socialPlayer.getMainChannel()).message(text(page)).build())
            );

            bookMeta.setPage(i, parsedPage);
        }

        event.setNewBookMeta(bookMeta);
    }

}
