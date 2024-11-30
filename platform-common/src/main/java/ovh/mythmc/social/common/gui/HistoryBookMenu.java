package ovh.mythmc.social.common.gui;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;

public interface HistoryBookMenu extends BookMenu {

    @Override
    default Book book(SocialContext context) {
        if (context instanceof SocialHistoryMenuContext menuContext)
            return book(menuContext);
        
        return null;
    }

    Book book(SocialHistoryMenuContext context);

    @Override
    default Component header(SocialContext context) {
        if (context instanceof SocialHistoryMenuContext menuContext)
            return header(menuContext);

        return Component.empty();
    }

    default Component header(SocialHistoryMenuContext context) {
        return Component.empty();
    }
    
}
