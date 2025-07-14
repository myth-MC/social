package ovh.mythmc.social.common.text.placeholder.chat;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

public final class ChannelIconPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "channel_icon";
    }

    @Override
    public Component get(SocialParserContext context) {
        return context.channel().icon();
    }

}
