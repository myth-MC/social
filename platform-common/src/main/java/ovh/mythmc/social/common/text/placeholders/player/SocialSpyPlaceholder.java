package ovh.mythmc.social.common.text.placeholders.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

public final class SocialSpyPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "socialspy";
    }

    @Override
    public Component get(SocialParserContext context) {
        if (context.socialPlayer().isSocialSpy()) {
            return Component.text("true", NamedTextColor.GREEN);
        }

        return Component.text("false", NamedTextColor.RED);
    }

}
