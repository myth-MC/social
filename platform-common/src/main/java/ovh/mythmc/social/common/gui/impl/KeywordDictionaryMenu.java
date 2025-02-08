package ovh.mythmc.social.common.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualKeyword;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.SimpleBookMenu;

public final class KeywordDictionaryMenu implements SimpleBookMenu {

    @Override
    public Component header(SocialMenuContext context) {
        Component headerFromConfig = Component.empty();
        for (Component line : Social.get().getConfig().getMenus().getKeywordDictionary().getHeader()) {
            headerFromConfig = headerFromConfig.append(line).appendNewline();
        }

        return headerFromConfig
            .appendNewline();
    }

    @Override
    public Book book(SocialMenuContext context) {
        Component title = Component.text("Keyword Dictionary");
        Component author = Component.text("social (myth-MC)");
        List<DictionaryPage> pages = new ArrayList<>();

        List<SocialContextualKeyword> keywords = Social.get().getTextProcessor().getContextualParsers().stream()
            .filter(parser -> parser instanceof SocialContextualKeyword)
            .map(parser -> (SocialContextualKeyword) parser)
            .collect(Collectors.toList());

        for (SocialContextualKeyword keyword : keywords) {
            if (pages.size() > 0 && pages.get(pages.size() -1).keywords.size() < Social.get().getConfig().getMenus().getKeywordDictionary().getMaxKeywordsPerPage()) {
                pages.get(pages.size() -1).keywords.add(keyword);
                continue;
            }

            pages.add(new DictionaryPage(List.of(keyword)));
        }

        return Book.book(title, author, pages.stream().map(page -> page.asComponent(context)).toList());
    }

    private class DictionaryPage {

        private final Collection<SocialContextualKeyword> keywords = new ArrayList<>();

        private DictionaryPage(Collection<SocialContextualKeyword> keywords) {
            this.keywords.addAll(keywords);
        }

        private Component asComponent(SocialMenuContext context) {
            Component page = Component.empty();

            for (SocialContextualKeyword keyword : keywords) {
                SocialParserContext parserContext = SocialParserContext.builder(context.viewer(), Component.text("[" + keyword.keyword() + "]")).build();

                page = page.append(Component.empty()
                    .clickEvent(ClickEvent.copyToClipboard("[" + keyword.keyword() + "] "))
                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getKeywordDictionary().getCopyToClipboard())))                
                    .append(Component.text("[", NamedTextColor.DARK_GRAY))
                    .append(Component.text(keyword.keyword(), NamedTextColor.BLUE))
                    .append(Component.text("]", NamedTextColor.DARK_GRAY))
                    .appendNewline()
                    .append(getField(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getKeywordDictionary().getResult()), keyword.process(parserContext)))
                    .appendNewline()
                    .appendNewline()
                );
            }

            return page;
        }

    }
    
}
