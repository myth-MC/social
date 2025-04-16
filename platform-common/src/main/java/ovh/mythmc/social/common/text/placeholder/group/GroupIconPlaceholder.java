package ovh.mythmc.social.common.text.placeholder.group;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
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
        final var optionalGroup = context.user().group();
        return optionalGroup.map(ChatChannel::icon).orElseGet(Component::empty);

    }

}
