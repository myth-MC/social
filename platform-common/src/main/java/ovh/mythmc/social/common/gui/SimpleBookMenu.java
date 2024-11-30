package ovh.mythmc.social.common.gui;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialContext;
import ovh.mythmc.social.common.context.SocialMenuContext;

public interface SimpleBookMenu extends BookMenu {

    @Override
    default Book book(SocialContext context) {
        if (context instanceof SocialMenuContext menuContext)
            return book(menuContext);
            
        return null;
    }

    Book book(SocialMenuContext context);

    @Override
    default Component header(SocialContext context) {
        if (context instanceof SocialMenuContext menuContext)
            return header(menuContext);

        return Component.empty();
    }

    default Component header(SocialMenuContext context) {
        return Component.empty();
    }
    
}
