package ovh.mythmc.social.common.text.placeholder.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.api.text.parser.SocialContextualPlaceholder;
import ovh.mythmc.social.common.text.parser.MiniMessageParser;

public final class ClickableChannelIconPlaceholder extends SocialContextualPlaceholder {

    @Override
    public String identifier() {
        return "clickable_channel_icon";
    }

    @Override
    public Component get(SocialParserContext context) {
        return context.channel().icon()
            .hoverEvent(getChannelHoverText(context))
            .clickEvent(ClickEvent.runCommand("/social:social channel " + context.channel().name()));
    }

    private static Component getChannelHoverText(SocialParserContext context) {
        Component channelHoverText = null;
        if (context.channel().description() != null) {
            channelHoverText = Component.empty()
                .append(Component.text(Social.get().getConfig().getChat().getChannelHoverText()))
                .appendNewline()
                .append(context.channel().description());

            return SocialContextualParser.request(context.withMessage(channelHoverText),
                ChannelIconPlaceholder.class,
                ChannelPlaceholder.class,
                MiniMessageParser.class
            );
        }

        return channelHoverText;
    }

}
