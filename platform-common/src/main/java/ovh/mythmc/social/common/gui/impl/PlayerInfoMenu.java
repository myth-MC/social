package ovh.mythmc.social.common.gui.impl;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.context.SocialMenuContext;
import ovh.mythmc.social.common.gui.SimpleBookMenu;

public final class PlayerInfoMenu implements SimpleBookMenu {

    @Override
    public Component header(SocialMenuContext context) {
        Component headerFromConfig = Component.empty();
        for (Component line : Social.get().getConfig().getMenus().getPlayerInfo().getHeader()) {
            headerFromConfig = headerFromConfig.append(line).appendNewline();
        }

        return headerFromConfig
            .appendNewline();
    }

    @Override
    public Book book(SocialMenuContext context) {
        Component title = Component.text("Player Information");
        Component author = Component.text("social (myth-MC)");

        Component alias = getField(
            MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getAlias()), 
            Component.text(context.target().getCachedNickname())
        );

        Component username = getField(
            MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getUsername()), 
            Component.text(context.target().player().get().getName())
        );

        Component messageCount = getField(
            MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getMessageCount()), 
            Component.text(Social.get().getChatManager().getHistory().getByUser(context.target()).size())
        );

        Component mainChannel = getField(
            MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getMainChannel()),
            Component.text(context.target().getMainChannel().getName())
        );

        Component visibleChannels = getField(
            MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getVisibleChannels()),
            Component.empty()
        );

        Component visibleChannelsHoverText = MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getVisibleChannelsHoverText());

        for (ChatChannel channel : Social.get().getChatManager().getVisibleChannels(context.target())) {
            SocialParserContext parserContext = SocialParserContext.builder(context.target(), Component.text(channel.getIcon() + " " + channel.getName(), channel.getColor()))
                .build();

            visibleChannelsHoverText = visibleChannelsHoverText
                .appendNewline()
                .append(getField(
                    Social.get().getTextProcessor().parse(parserContext), 
                    null
                ));
        }

        Component playerInfo = Component.empty()
            .append(username)
            .appendNewline()
            .append(alias)
            .appendNewline()
            .append(mainChannel)
            .appendNewline()
            .append(messageCount
                .hoverEvent(MiniMessage.miniMessage().deserialize(Social.get().getConfig().getMenus().getPlayerInfo().getClickToSeeMessageHistory()).asHoverEvent())
                .clickEvent(ClickEvent.runCommand("/social:social history player " + context.target().player().get().getName()))
            )
            .appendNewline()
            .append(visibleChannels
                .hoverEvent(visibleChannelsHoverText.asHoverEvent())
            );

        return Book.book(title, author, playerInfo);
    }
    
}
