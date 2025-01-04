package ovh.mythmc.social.common.text.formatters;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.formatters.SocialSurroundingFormatter;

public final class StrikethroughTextFormatter extends SocialSurroundingFormatter {

    @Override
    public String characters() {
        return "~~";
    }

    @Override
    public Component format(SocialParserContext context) {
        return context.message().decorate(TextDecoration.STRIKETHROUGH);
    }
    
}
