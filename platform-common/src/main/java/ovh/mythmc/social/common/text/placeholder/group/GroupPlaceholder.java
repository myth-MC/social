package ovh.mythmc.social.common.text.placeholder.group;

import net.kyori.adventure.text.Component;
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
        final var optionalGroup = context.user().group();
        return optionalGroup.map(groupChatChannel -> Component.text(groupChatChannel.aliasOrName())).orElseGet(Component::empty);

    }

}
