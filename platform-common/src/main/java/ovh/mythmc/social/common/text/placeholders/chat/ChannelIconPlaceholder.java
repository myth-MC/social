package ovh.mythmc.social.common.text.placeholders.chat;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

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
        ChatChannel chatChannel = null;
        if (context.playerChannel() != null) {
            chatChannel = context.playerChannel();
        } else {
            chatChannel = context.socialPlayer().getMainChannel();
        }

        return Component.text(chatChannel.getIcon());
    }

}
