package ovh.mythmc.social.common.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.SimpleBookMenu;

public final class EmojiDictionaryMenu implements SimpleBookMenu {

    private final int maxEmojisPerPage = 3;

    @Override
    public Component header(SocialMenuContext context) {
        return Component.text("╒═══════════╕", NamedTextColor.DARK_GRAY)
            .appendNewline()
            .append(Component.text(" |         ᴇᴍᴏᴊɪѕ         |", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .append(Component.text("╘═══════════╛", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .appendNewline();
    }

    @Override
    public Book book(SocialMenuContext context) {
        Component title = Component.text("Emoji Dictionary");
        Component author = Component.text("social (myth-MC)");
        List<DictionaryPage> pages = new ArrayList<>();

        for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
            if (pages.size() > 0 && pages.getLast().emojis.size() < maxEmojisPerPage) {
                pages.getLast().emojis.add(emoji);
                continue;
            }

            pages.add(new DictionaryPage(List.of(emoji)));
        }

        return Book.book(title, author, pages.stream().map(page -> page.asComponent()).toList());
    }

    private class DictionaryPage {

        private final Collection<Emoji> emojis = new ArrayList<>();

        private DictionaryPage(Collection<Emoji> emojis) {
            this.emojis.addAll(emojis);
        }

        private Component asComponent() {
            Component page = Component.empty();

            for (Emoji emoji : emojis) {
                page = page.append(emoji.asDescription(NamedTextColor.BLUE, NamedTextColor.DARK_GRAY, false)
                    .clickEvent(ClickEvent.copyToClipboard(":" + emoji.name() + ": "))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy to clipboard", NamedTextColor.LIGHT_PURPLE)))
                    .appendNewline()
                    .appendNewline());
            }

            return page;
        }

    }

}
