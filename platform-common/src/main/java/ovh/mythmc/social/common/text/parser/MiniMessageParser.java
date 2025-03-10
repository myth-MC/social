package ovh.mythmc.social.common.text.parser;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;

public final class MiniMessageParser implements SocialContextualParser {

    @Override
    public boolean supportsOfflinePlayers() {
        return true;
    }

    @Override
    public Component parse(SocialParserContext context) {
        Component message = miniMessage(context.message());

        if (message.hoverEvent() != null) {
            if (message.hoverEvent().value() instanceof Component hoverText)
                message = message.hoverEvent(HoverEvent.showText(miniMessage(hoverText)));
        }

        return message;
    }

    private static Component miniMessage(@NotNull Component component) {
        String serialized = MiniMessage.miniMessage().serialize(component);
        return MiniMessage.miniMessage().deserialize(serialized
            .replace("\\<", "<") // Possibly the worst way to achieve this (not even supported by MiniMessage's team, so plz don't do it)
        );
    }
    
}
