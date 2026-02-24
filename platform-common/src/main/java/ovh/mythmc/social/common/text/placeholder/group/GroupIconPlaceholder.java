package ovh.mythmc.social.common.text.placeholder.group;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.util.Mutable;

public final class GroupIconPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "group_icon";
    }

    @Override
    public Component get(SocialParserContext context) {
        final Mutable<GroupChatChannel> groupChannel = context.user().groupChannel();
        if (groupChannel.isEmpty()) {
            return Component.empty();
        }

        return groupChannel.get().icon();
    }

}
