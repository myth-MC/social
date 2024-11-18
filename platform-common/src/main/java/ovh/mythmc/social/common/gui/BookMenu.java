package ovh.mythmc.social.common.gui;

import net.kyori.adventure.inventory.Book;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.context.SocialMenuContext;

public interface BookMenu extends Menu {
    
    Book book();

    @Override
    default void open(SocialMenuContext context) {
        SocialAdventureProvider.get().player(context.socialPlayer().getPlayer()).openBook(book());
    }

}
