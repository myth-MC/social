package ovh.mythmc.social.api.chat.renderer.defaults;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class ConsoleChatRenderer implements SocialChatRenderer<Audience> {

    @Override
    public SocialRendererContext render(Audience target, SocialRegisteredMessageContext context) {
        // Set variables
        final AbstractSocialUser sender = context.sender();
        final ChatChannel channel = context.channel();
        final String rawMessage = context.rawMessage();

        // Reply icon
        final Component replyIcon = SocialChatRendererUtil.getReplyIcon(sender, context);

        // Get sender's nickname
        final Component nickname = Component.text(sender.name());

        // Get channel icon
        final Component channelIcon = SocialChatRendererUtil.getClickableChannelIcon(sender, channel);

        // Get channel divider
        final Component textDivider = Component.text(channel.getTextDivider());

        // Render message prefix (channel icon, reply icon, display name, text divider...)
        Component renderedPrefix = Social.get().getTextProcessor().parse(
            SocialParserContext.builder(sender, Component.empty()
                .append(channelIcon)
                .appendSpace()
                .append(replyIcon)
                .append(nickname)
                .appendSpace()
                .append(textDivider)
                .appendSpace())
            .build());

        return new SocialRendererContext(
            sender, 
            channel,
            context.viewers(), 
            renderedPrefix, 
            rawMessage,
            Component.text(rawMessage),
            context.replyId(),
            context.id()
        );
    }
    
}
