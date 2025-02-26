package ovh.mythmc.social.common.text.formatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.formatter.SocialSurroundingFormatter;

public final class ObfuscatedTextFormatter extends SocialSurroundingFormatter {

    @Override
    public String characters() {
        return "||";
    }

    @Override
    public Component format(SocialParserContext context) {
        return context.message()
            .decorate(TextDecoration.OBFUSCATED)
            .hoverEvent(HoverEvent.showText(context.message()));
    }
    
}
