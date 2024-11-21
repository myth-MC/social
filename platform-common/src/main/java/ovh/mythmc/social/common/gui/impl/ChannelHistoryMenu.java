package ovh.mythmc.social.common.gui.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.common.context.SocialHistoryMenuContext;
import ovh.mythmc.social.common.gui.HistoryBookMenu;

public class ChannelHistoryMenu implements HistoryBookMenu {

    private final int maxMessagesPerPage = 6;

    @Override
    public Component header(SocialHistoryMenuContext context) {
        Component channel = Component.text("*", NamedTextColor.GRAY);
        if (context.channel() != null) {
            TextColor channelColor = context.channel().getColor();
            if (channelColor.equals(NamedTextColor.YELLOW))
                channelColor = NamedTextColor.GOLD;

            channel = Component.text(context.channel().getName(), channelColor);
        }

        return Component.text("╒═══════════╕", NamedTextColor.DARK_GRAY)
            .appendNewline()
            .append(Component.text(" |        ʜɪѕᴛᴏʀʏ        |", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .append(Component.text("╘═══════════╛", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .appendNewline()
            .append(Component.empty()
                .append(Component.text("Scope:", NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(channel)
            )
            .appendNewline()
            .appendNewline();
   }

    @Override
    public Book book(SocialHistoryMenuContext context) {
        Component title = Component.text("Global Chat History");
        Component author = Component.text("social (myth-MC)");

        List<HistoryPage> pages = new ArrayList<>();

        List<SocialMessageContext> history = null;
        if (context.channel() == null) {
            history = Social.get().getChatManager().getHistory().get();
        } else {
            history = Social.get().getChatManager().getHistory().getByChannel(context.channel());
        }

        for (SocialMessageContext message : history.reversed()) {
            if (pages.size() > 0 && pages.getLast().messages.size() < maxMessagesPerPage) {
                pages.getLast().messages.add(message);
                continue;
            }

            pages.add(new HistoryPage(List.of(message)));
        }

        return Book.book(title, author, pages.stream().map(page -> page.asComponent()).toList());
    }

    private class HistoryPage {

        private final Collection<SocialMessageContext> messages = new ArrayList<>();

        private HistoryPage(Collection<SocialMessageContext> messages) {
            this.messages.addAll(messages);
        }

        private Component asComponent() {
            Component page = Component.empty();

            for (SocialMessageContext message : messages) { 
                Component hoverText = Component.text(message.sender().getNickname() + ": ", NamedTextColor.GRAY)
                    .append(Component.text(message.rawMessage(), NamedTextColor.WHITE))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Context:", NamedTextColor.BLUE))
                    .appendNewline()
                    .append(getField(Component.text("Channel"), Component.text(message.chatChannel().getName(), message.chatChannel().getColor())));

                if (message.isReply()) {
                    SocialMessageContext reply = Social.get().getChatManager().getHistory().getById(message.replyId());
                    Component replyMessage = Component.text(reply.rawMessage())
                        .appendSpace()
                        .append(Component.text("(" + reply.sender().getNickname() + ")", NamedTextColor.BLUE));

                    hoverText = hoverText
                        .appendNewline()
                        .append(getField(Component.text("Reply to"), replyMessage));
                }

                page = page.append(Component.empty()
                    .append(Component.text("#" + message.id(), NamedTextColor.DARK_GRAY))
                    .appendSpace()
                    .append(Component.text("(" + message.date() + ")", NamedTextColor.BLUE))
                    .appendNewline()
                    .hoverEvent(HoverEvent.showText(hoverText)));
            }

            return page;
        }

    }
    
    
}
