package ovh.mythmc.social.common.text.placeholders.prefix;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;

public final class ErrorPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "error_prefix";
    }

    @Override
    public Component get(SocialParserContext context) {
        Component prefix = Component.text(Social.get().getConfig().getMessages().getErrorPrefix());
        return request(context.withMessage(prefix), MiniMessageParser.class);
    }

}
