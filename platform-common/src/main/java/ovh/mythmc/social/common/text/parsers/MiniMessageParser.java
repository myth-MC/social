package ovh.mythmc.social.common.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;

@SocialParserProperties(priority = SocialParserProperties.ParserPriority.LOW)
public final class MiniMessageParser implements SocialContextualParser {

    @Override
    public Component parse(SocialParserContext context) {
        String serialized = MiniMessage.miniMessage().serialize(context.message());
        return MiniMessage.miniMessage().deserialize(serialized
            .replace("\\<", "<") // Please do not ever do this
        );
    }
    
}
