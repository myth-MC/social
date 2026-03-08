package ovh.mythmc.social.common.text.placeholder.group;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;

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
        final Optional<GroupChatChannel> groupChannel = context.user().groupChannel();
        if (groupChannel.isEmpty()) {
            return Component.empty();
        }

        return Component.text(groupChannel.get().aliasOrName());
    }

}
