package ovh.mythmc.social.common.text.placeholders.player;

import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;

public final class UsernamePlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "username";
    }

    @Override
    public Component get(SocialParserContext context) {
        return Component.text(context.user().player().get().getName());
    }

}
