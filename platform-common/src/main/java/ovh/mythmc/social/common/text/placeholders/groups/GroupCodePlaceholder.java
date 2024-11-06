package ovh.mythmc.social.common.text.placeholders.groups;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.CustomTextProcessor;
import ovh.mythmc.social.api.text.parsers.SocialContextualPlaceholder;

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
        GroupChatChannel groupChatChannel = Social.get().getChatManager().getGroupChannelByPlayer(context.socialPlayer());
        if (groupChatChannel == null)
            return Component.empty();

        CustomTextProcessor textProcessor = CustomTextProcessor.defaultProcessor()
            .withExclusions(context.appliedParsers());

        Component hoverText = textProcessor.parse(context.withMessage(Component.text(Social.get().getConfig().getSettings().getChat().getGroups().getCodeHoverText())));

        return Component.text(groupChatChannel.getCode())
            .hoverEvent(HoverEvent.showText(hoverText))
            .clickEvent(ClickEvent.copyToClipboard(groupChatChannel.getCode() + ""));
    }

}
