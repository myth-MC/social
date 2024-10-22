package ovh.mythmc.social.common.text.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.annotations.SocialParserProperties;
import ovh.mythmc.social.api.text.parsers.SocialParser;

@SocialParserProperties(priority = SocialParserProperties.ParserPriority.LOW)
public final class MiniMessageParser implements SocialParser {

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        String serialized = MiniMessage.miniMessage().serialize(message);
        return MiniMessage.miniMessage().deserialize(serialized
            .replace("\\<", "<") // Please do not ever do this
        );
    }
    
}
