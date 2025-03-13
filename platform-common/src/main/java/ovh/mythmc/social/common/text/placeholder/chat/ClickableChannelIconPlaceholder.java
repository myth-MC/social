package ovh.mythmc.social.common.text.placeholder.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class ClickableChannelIconPlaceholder extends SocialContextualPlaceholder {

    @Override
    public String identifier() {
        return "clickable_channel_icon";
    }

    @Override
    public Component get(SocialParserContext context) {
        return context.channel().icon()
            .hoverEvent(getChannelHoverText(context.user(), context.channel()))
            .clickEvent(ClickEvent.runCommand("/social:social channel " + context.channel().name()));
    }

    private static Component getChannelHoverText(AbstractSocialUser user, ChatChannel channel) {
        Component channelHoverText = Component.empty();
        if (channel.description() != null)
            channelHoverText = channelHoverText
                .append(Component.text(Social.get().getConfig().getChat().getChannelHoverText()))
                .appendNewline()
                .append(channel.description());

        return channelHoverText;
    }

}
