package ovh.mythmc.social.common.text.placeholders.groups;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;

public final class GroupCodePlaceholder extends SocialContextualPlaceholder {

    @Override
    public boolean legacySupport() {
        return true;
    }

    @Override
    public String identifier() {
        return "group_code";
    }

    @Override
    public Component get(SocialParserContext context) {
        GroupChatChannel groupChatChannel = context.user().getGroupChatChannel();
        if (groupChatChannel == null)
            return Component.empty();

        Component hoverText = Component.text(Social.get().getConfig().getChat().getGroups().getCodeHoverText());
        hoverText = SocialContextualParser.request(context.withMessage(hoverText), 
            MiniMessageParser.class
        );
        
        return Component.text(groupChatChannel.getCode())
            .hoverEvent(HoverEvent.showText(hoverText))
            .clickEvent(ClickEvent.copyToClipboard(groupChatChannel.getCode() + ""));
    }

}
