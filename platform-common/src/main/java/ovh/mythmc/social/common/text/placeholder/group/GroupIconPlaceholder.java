package ovh.mythmc.social.common.text.placeholder.group;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

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
        GroupChatChannel groupChatChannel = context.user().getGroupChatChannel();
        if (groupChatChannel == null)
            return Component.empty();

        return Component.text(groupChatChannel.getIcon());
    }

}
