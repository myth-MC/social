package ovh.mythmc.social.common.text.placeholders.prefix;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

public final class PrivateMessagePlaceholder extends SocialContextualPlaceholder {
    
    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "private_message_prefix";
    }

    @Override
    public Component get(SocialParserContext context) {
        return Component.text(Social.get().getConfig().getSettings().getCommands().getPrivateMessage().prefix());
    }

}
