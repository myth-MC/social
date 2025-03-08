package ovh.mythmc.social.common.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.common.context.SocialMenuContext;

public interface BookMenu extends Menu {
    
    Book book(SocialContext context);

    @Override
    default void open(SocialContext context) {
        if (context instanceof SocialMenuContext menuContext) {
            List<Component> pages = format(menuContext, book(menuContext).pages());

            menuContext.viewer().openBook(book(menuContext).pages(pages));
        }
    }

    default List<Component> format(SocialContext context, List<Component> pages) {
        List<Component> formattedPages = new ArrayList<>(pages);

        if (formattedPages.isEmpty())
            formattedPages.add(emptyPage(context));

        formattedPages = formattedPages.stream()
            .map(page -> header(context).append(page))
            .collect(Collectors.toList());

        return formattedPages;
    }

    default Component emptyPage(SocialContext context) {
        return Component.text("             ?", NamedTextColor.DARK_GRAY)
            .hoverEvent(HoverEvent.showText(Component.text("N/A", NamedTextColor.RED)));
    }

    default Component header(SocialContext context) {
        return Component.empty();
    }

    default Component getField(Component key, Component value) {
        Component field = Component.text("└", NamedTextColor.DARK_GRAY)
            .appendSpace()
            .append(key.colorIfAbsent(NamedTextColor.WHITE));

        if (value != null && !value.equals(Component.empty()))
            field = field
                .append(Component.text(":", NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(value.colorIfAbsent(NamedTextColor.GRAY));

        return field;
    }

}
