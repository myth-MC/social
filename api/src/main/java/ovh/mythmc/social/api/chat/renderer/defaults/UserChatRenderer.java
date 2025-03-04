package ovh.mythmc.social.api.chat.renderer.defaults;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

public class UserChatRenderer implements SocialChatRenderer<SocialUser> {

    @Override
    public SocialRendererContext render(SocialUser target, SocialRegisteredMessageContext context) {
        // Set variables
        final SocialUser sender = context.sender();
        final ChatChannel channel = context.channel();

        // Get channel icon
        final Component channelIcon = SocialChatRendererUtil.getClickableChannelIcon(sender, channel);

        // Reply icon
        final Component replyIcon = SocialChatRendererUtil.getReplyIcon(sender, context);

        // Get sender's nickname
        final Component nickname = SocialChatRendererUtil.getNicknameWithColor(sender, channel);

        // Get channel divider
        final Component textDivider = Component.text(channel.getTextDivider());

        // Render message prefix (channel icon, reply icon, display name, text divider...)
        var prefix = Component.empty()
            .append(channelIcon)
            .appendSpace()
            .append(replyIcon)
            .append(nickname)
            .appendSpace()
            .append(textDivider)
            .appendSpace();

        var renderedPrefix = Social.get().getTextProcessor().parse(SocialParserContext.builder(sender, prefix).channel(channel).build());

        // Send as channelable message to companion mod
        if (target.companion().isPresent())
            renderedPrefix = CompanionModUtils.asChannelable(renderedPrefix, channel);

        return new SocialRendererContext(
            sender, 
            channel,
            context.viewers(), 
            renderedPrefix, 
            context.rawMessage(),
            context.message(),
            context.replyId(),
            context.id()
        );
    }

}
