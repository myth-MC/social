package ovh.mythmc.social.common.text.placeholder.player;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

public final class FormattedNicknamePlaceholder extends SocialContextualPlaceholder {

    @Override
    public String identifier() {
        return "formatted_nickname";
    }

    @Override
    public Component get(SocialParserContext context) {
        if (context.group().isEmpty())
            return context.message();

        String playerNicknameFormat = Social.get().getConfig().getChat().getPlayerNicknameFormat();
        return context.group().get().requestToGroup(this, context.withMessage(Component.text(playerNicknameFormat)));
    }
    
}
