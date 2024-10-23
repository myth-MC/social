package ovh.mythmc.social.api.text.parsers;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.players.SocialPlayer;

@SuppressWarnings("deprecation") // Extending SocialParser is necessary to assure legacy compatibility
public interface SocialContextualParser extends SocialParser {
    
    Component parse(SocialParserContext context);

    @Override
    default Component parse(SocialPlayer socialPlayer, Component message) {
        return Component.empty();
    }

}
