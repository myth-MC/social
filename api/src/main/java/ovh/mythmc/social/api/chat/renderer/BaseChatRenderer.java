package ovh.mythmc.social.api.chat.renderer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRendererContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.users.SocialUser;

public class BaseChatRenderer implements SocialChatRenderer {

    @Override
    public SocialRendererContext process(SocialMessageContext context) {
        // Set variables
        SocialUser sender = context.sender();
        ChatChannel channel = sender.getMainChannel();
        Set<Audience> viewers = new HashSet<>();
        String rawMessage = context.rawMessage();

        // Check if message is a reply
        Integer replyId = null;
        if (rawMessage.startsWith("(re:#") && rawMessage.contains(")")) {
            String replyIdString = rawMessage.substring(5, rawMessage.indexOf(")"));
            replyId = tryParse(replyIdString);
            rawMessage = rawMessage.replace("(re:#" + replyId + ")", "").trim();
        }

        if (rawMessage.isBlank())
            return null;

        // Prepare message event
        SocialChatMessagePrepareEvent chatMessagePrepareEvent = SocialChatRendererUtil.messagePrepareEvent(sender, channel, rawMessage, replyId);
        if (chatMessagePrepareEvent.isCancelled())
            return null; // CANCEL

        // Reply icon
        Component replyIcon = SocialChatRendererUtil.getReplyIcon(sender, chatMessagePrepareEvent);

        // Update event fields
        channel = chatMessagePrepareEvent.getChannel();
        rawMessage = chatMessagePrepareEvent.getRawMessage();
        replyId = chatMessagePrepareEvent.getReplyId();

        // Get viewers
        Collection<UUID> members = SocialChatRendererUtil.channelMembersWithSocialSpy(channel);

        // Set viewers
        viewers.addAll(members.stream().map(uuid -> Social.get().getUserManager().get(uuid)).toList());

        // Add console
        viewers.add(SocialAdventureProvider.get().console());

        // Get sender's nickname
        Component nickname = SocialChatRendererUtil.getNicknameWithColor(sender, channel);

        // Get channel icon
        Component channelIcon = SocialChatRendererUtil.getClickableChannelIcon(sender, channel);

        // Get channel divider
        Component textDivider = Component.text(channel.getTextDivider());

        // Register message in history and get ID
        Integer messageId = Social.get().getChatManager().getHistory().register(SocialMessageContext.builder()
            .sender(sender)
            .rawMessage(rawMessage)
            .chatChannel(channel)
            .replyId(replyId)
            .build());

        // Get filtered message
        Component filteredMessage = Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder()
            .user(sender)
            .message(Component.text(rawMessage))
            .build());

        // Render message prefix (channel icon, reply icon, display name, text divider...)
        Component renderedPrefix = Social.get().getTextProcessor().parse(SocialParserContext.builder()
            .user(sender)
            .message(
                Component.empty()
                    .append(channelIcon)
                    .appendSpace()
                    .append(replyIcon)
                    .append(nickname)
                    .appendSpace()
                    .append(textDivider)
                    .appendSpace()
            )
            .build()
        );

        // Get ID to reply to this message
        Integer idToReply = messageId;
        if (replyId != null)
            idToReply = Integer.min(replyId, idToReply);

        return new SocialRendererContext(
            sender, 
            channel,
            viewers, 
            renderedPrefix, 
            rawMessage,
            filteredMessage.applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + idToReply + ") "))),
            idToReply,
            messageId
        );
    }

    private Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
}
