package ovh.mythmc.social.common.text.placeholder.chat;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

public final class ChannelPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "channel";
    }

    @Override
    public Component get(SocialParserContext context) {
        ChatChannel chatChannel = context.channel();
        
        String channelName = chatChannel.getName();
        if (chatChannel instanceof GroupChatChannel groupChatChannel && groupChatChannel.getAlias() != null)
            channelName = groupChatChannel.getAlias();

        return Component.text(channelName)
            .color(chatChannel.getColor());
    }

}
