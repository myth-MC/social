package ovh.mythmc.social.bukkit.callback.game.invoker;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.BookEdit;
import ovh.mythmc.social.common.callback.game.BookEditCallback;

public class BookEditInvoker implements Listener {

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        final var user = BukkitSocialUser.from(event.getPlayer());
        if (user == null)
            return;

        final BookMeta bookMeta = event.getNewBookMeta();
        final List<Component> pages = bookMeta.getPages().stream()
            .map(page -> Component.text(page))
            .collect(Collectors.toList());

        BookEditCallback.INSTANCE.invoke(new BookEdit(user, pages), result -> {
            for (int i = 0; i < result.pages().size(); i++) {
                final String page = PlainTextComponentSerializer.plainText()
                    .serialize(result.pages().get(i));

                bookMeta.setPage(i, page);
            }

            event.setNewBookMeta(bookMeta);
        });
    }
    
}
