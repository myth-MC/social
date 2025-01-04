package ovh.mythmc.social.common.text.placeholders.groups;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

public final class GroupPlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "group";
    }

    @Override
    public Component get(SocialParserContext context) {
        GroupChatChannel groupChatChannel = context.user().getGroupChatChannel();
        if (groupChatChannel == null)
            return Component.empty();

        if (groupChatChannel.getAlias() != null)
            return Component.text(groupChatChannel.getAlias());

        return Component.text(groupChatChannel.getName());
    }

}
