package ovh.mythmc.social.common.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext.HeaderType;
import ovh.mythmc.social.common.gui.HistoryBookMenu;

public class HistoryMenu implements HistoryBookMenu {
    
    @Override
    public Component header(SocialHistoryMenuContext context) {
        Component headerFromConfig = Component.empty();
        for (Component line : Social.get().getConfig().getMenus().getChatHistory().getHeader()) {
            headerFromConfig = headerFromConfig.append(line).appendNewline();
        }

        Component scope = Component.text("*", NamedTextColor.GRAY);

        switch (context.headerType()) {
            case CHANNEL:
                if (context.channel() != null) {
                    TextColor channelColor = context.channel().getColor();
                    if (channelColor.equals(NamedTextColor.YELLOW))
                        channelColor = NamedTextColor.GOLD;
        
                    scope = Component.text(context.channel().getName(), channelColor);
                }
                break;
            case PLAYER:
                if (context.target() != null) {
                    scope = Social.get().getTextProcessor().parse(
                        SocialParserContext.builder(context.target(), Component.text("$(nickname)", NamedTextColor.BLUE))
                            .build()
                    );
                }
                break;
            case THREAD:
                if (context.messages() != null && !context.messages().isEmpty())
                    scope = Component.text("#" + context.messages().get(0).id(), NamedTextColor.BLUE);
                break;
            default:
                break;
        }

        return headerFromConfig
            .appendNewline()
            .append(Component.empty()
                .append(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getScope()).colorIfAbsent(NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(scope
                    .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getClickToOpenGlobalHistory())))
                    .clickEvent(ClickEvent.runCommand("/social:social history"))
                )
            )
            .appendNewline()
            .appendNewline();
   }

    @Override
    public Book book(SocialHistoryMenuContext context) {
        Component title = Component.text("Chat History");
        Component author = Component.text("social (myth-MC)");

        List<HistoryPage> pages = new ArrayList<>();
        List<SocialRegisteredMessageContext> messages = new ArrayList<>(context.messages());
        Collections.reverse(messages);
        for (SocialRegisteredMessageContext message : messages) {
            if (!pages.isEmpty() && pages.get(pages.size() -1).messages.size() < Social.get().getConfig().getMenus().getChatHistory().getMaxMessagesPerPage()) {
                pages.get(pages.size() -1).messages.add(message);
                continue;
            }

            pages.add(new HistoryPage(List.of(message)));
        }

        return Book.book(title, author, pages.stream().map(page -> page.asComponent(context)).toList());
    }

    private class HistoryPage {

        private final Collection<SocialRegisteredMessageContext> messages = new ArrayList<>();

        private HistoryPage(Collection<SocialRegisteredMessageContext> messages) {
            this.messages.addAll(messages);
        }

        private Component asComponent(SocialHistoryMenuContext context) {
            Component page = Component.empty();

            for (SocialRegisteredMessageContext message : messages) { 
                Component hoverText = Component.text(message.sender().cachedDisplayName() + ": ", NamedTextColor.GRAY)
                    .append(message.message()).color(NamedTextColor.WHITE)
                    .appendNewline()
                    .appendNewline()
                    .append(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getContext()).colorIfAbsent(NamedTextColor.BLUE))
                    .appendNewline()
                    .append(getField(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getContextChannel()), Component.text(message.channel().getName(), message.channel().getColor())));

                Component toAppend = Component.empty()
                    .append(Component.text("#" + message.id(), NamedTextColor.DARK_GRAY))
                    .appendSpace()
                    .append(Component.text("(" + message.date() + ")", NamedTextColor.BLUE))
                    .appendNewline();

                if (message.isReply() && context.headerType() != HeaderType.THREAD) {
                    SocialRegisteredMessageContext reply = Social.get().getChatManager().getHistory().getById(message.replyId());
                    toAppend = toAppend.clickEvent(ClickEvent.runCommand("/social:social history thread " + message.id()));

                    Component replyMessage = Component.text("#" + reply.id())
                        .appendSpace()
                        .append(Component.text("(" + reply.sender().cachedDisplayName() + ")", NamedTextColor.BLUE)
                        .appendNewline()
                        .appendNewline()
                        .append(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getClickToOpenThreadHistory()))
                    );

                    hoverText = hoverText
                        .appendNewline()
                        .append(getField(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getChatHistory().getContextReplyTo()), replyMessage));
                }

                page = page.append(toAppend.hoverEvent(HoverEvent.showText(hoverText)));
            }

            return page;
        }

    }
    
}
