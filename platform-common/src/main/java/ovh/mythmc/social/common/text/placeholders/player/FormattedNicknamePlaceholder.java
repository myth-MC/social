package ovh.mythmc.social.common.text.placeholders.player;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

public final class FormattedNicknamePlaceholder extends SocialContextualPlaceholder {

    @Override
    public String identifier() {
        return "formatted_nickname";
    }

    @Override
    public Component get(SocialParserContext context) {
        String playerNicknameFormat = Social.get().getConfig().getChat().getPlayerNicknameFormat();
        return context.group().requestToGroup(this, context.withMessage(Component.text(playerNicknameFormat)));
    }
    
}
