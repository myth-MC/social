package ovh.mythmc.social.common.gui;

import java.util.List;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;

public interface HistoryBookMenu extends Menu {

    Book book(SocialHistoryMenuContext context);

    @Override
    default void open(SocialContext context) {
        if (context instanceof SocialHistoryMenuContext menuContext) {
            // Append header
            List<Component> pages = book(menuContext).pages().stream().map(page -> header(menuContext).append(page)).toList();
            SocialAdventureProvider.get().player(menuContext.viewer().getPlayer()).openBook(book(menuContext).pages(pages));
        }
    }

    default Component header(SocialHistoryMenuContext context) {
        return Component.empty();
    }

    default Component getField(Component key, Component value) {
        return Component.text("└", NamedTextColor.DARK_GRAY)
            .appendSpace()
            .append(key.colorIfAbsent(NamedTextColor.WHITE))
            .append(Component.text(":", NamedTextColor.DARK_GRAY))
            .appendSpace()
            .append(value.colorIfAbsent(NamedTextColor.GRAY));
    }
    
}