package ovh.mythmc.social.common.text.placeholder.player;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

public final class NicknamePlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "nickname";
    }

    @Override
    public Component get(SocialParserContext context) {
        return Component.text(context.user().getNickname());
    }

}
