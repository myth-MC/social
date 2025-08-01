package ovh.mythmc.social.common.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emoji.Emoji;
import ovh.mythmc.social.api.util.registry.NamespacedRegistryKey;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.SimpleBookMenu;

public final class EmojiDictionaryMenu implements SimpleBookMenu {

    @Override
    public Component header(SocialMenuContext context) {
        Component headerFromConfig = Component.empty();
        for (Component line : Social.get().getConfig().getMenus().getEmojiDictionary().getHeader()) {
            headerFromConfig = headerFromConfig.append(line).appendNewline();
        }

        return headerFromConfig
            .appendNewline();
    }

    @Override
    public Book book(SocialMenuContext context) {
        Component title = Component.text("Emoji Dictionary");
        Component author = Component.text("social (myth-MC)");
        List<DictionaryPage> pages = new ArrayList<>();

        for (Map.Entry<NamespacedRegistryKey, Emoji> entry : Social.registries().emojis().registry().entrySet()) {
            if (entry.getKey().namespace().equalsIgnoreCase("hidden"))
                continue;

            if (!pages.isEmpty() && pages.get(pages.size() -1).emojis.size() < Social.get().getConfig().getMenus().getEmojiDictionary().getMaxEmojisPerPage()) {
                pages.get(pages.size() -1).emojis.add(entry);
                continue;
            }

            pages.add(new DictionaryPage(List.of(entry)));
        }

        return Book.book(title, author, pages.stream().map(DictionaryPage::asComponent).toList());
    }

    private class DictionaryPage {

        private final Collection<Map.Entry<NamespacedRegistryKey, Emoji>> emojis = new ArrayList<>();

        private DictionaryPage(Collection<Map.Entry<NamespacedRegistryKey, Emoji>> emojis) {
            this.emojis.addAll(emojis);
        }

        private Component asComponent() {
            Component page = Component.empty();

            for (final var entry : emojis) {
                page = page.append(entry.getValue().asDescription(NamedTextColor.BLUE, NamedTextColor.DARK_GRAY, false)
                    .clickEvent(ClickEvent.copyToClipboard(":" + entry.getValue().name() + ": "))
                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getEmojiDictionary().getCopyToClipboard())))
                    .appendNewline()
                    .append(getField(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getEmojiDictionary().getCategory()), Component.text(entry.getKey().namespace())))
                    .appendNewline()
                    .appendNewline());
            }

            return page;
        }

    }

}
