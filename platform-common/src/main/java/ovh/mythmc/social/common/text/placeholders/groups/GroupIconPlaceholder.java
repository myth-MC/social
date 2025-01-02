package ovh.mythmc.social.common.text.placeholders.groups;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

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
        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(context.user());
        if (groupChatChannel == null)
            return Component.empty();

        return Component.text(groupChatChannel.getIcon());
    }

}
