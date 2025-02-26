package ovh.mythmc.social.common.text.placeholder.chat;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

public final class ChannelNicknameColorPlaceholder extends SocialContextualPlaceholder {

    @Override
    public String identifier() {
        return "channel_nickname_color";
    }

    @Override
    public Component get(SocialParserContext context) {
        return Component.text(context.channel().getNicknameColor().asHexString());
    }
    
}
