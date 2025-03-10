package ovh.mythmc.social.api.chat.renderer;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.SocialInjectionParsers;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.context.SocialParserContext;

@UtilityClass
public class SocialChatRendererUtil {

    public Component getClickableChannelIcon(AbstractSocialUser user, ChatChannel channel) {
        return Component.text(channel.getIcon())
            .hoverEvent(getChannelHoverText(user, channel))
            .clickEvent(ClickEvent.runCommand("/social:social channel " + channel.getName()));
    }

    private Component getChannelHoverText(AbstractSocialUser user, ChatChannel channel) {
        Component channelHoverText = Component.empty();
        if (channel.isShowHoverText())
            channelHoverText = channelHoverText
                .append(Component.text(Social.get().getConfig().getChat().getChannelHoverText()))
                .appendNewline()
                .append(channel.getHoverText());

        return Social.get().getTextProcessor().parse(SocialParserContext.builder(user, channelHoverText).channel(channel).build());
    }

    public Component getNicknameWithColor(ChatChannel channel) {
        return Component.empty()
            .append(Component.text(Social.get().getConfig().getChat().getPlayerNicknameFormat()))
            .colorIfAbsent(channel.getNicknameColor());
    }

    public Component getReplyIcon(AbstractSocialUser sender, SocialRegisteredMessageContext message) {
        Component replyIcon = Component.empty();

        // Check that message is a reply
        if (!message.isReply())
            return replyIcon;

        // Check that sender has permission to send messages on this channel
        if (!Social.get().getChatManager().hasPermission(sender, message.channel()))
            return replyIcon;

        // Get the reply context
        final SocialRegisteredMessageContext reply = Social.get().getChatManager().getHistory().getById(message.replyId());

        // Icon hover text
        Component hoverText = Component.empty();
        for (SocialRegisteredMessageContext threadReply : Social.get().getChatManager().getHistory().getThread(reply, 8)) {
            hoverText = hoverText
                .append(threadReply.sender().displayName())
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(threadReply.message())
                .appendNewline();
        }

        // Show '...' if there are more replies in this thread
        if (Social.get().getChatManager().getHistory().getThread(reply, 9).size() >= 8) {
            hoverText = hoverText
                .append(Component.text("...", NamedTextColor.BLUE))
                .appendNewline();
        }

        // Show 'click to reply' text
        hoverText = hoverText
            .appendNewline()
            .append(
                Social.get().getTextProcessor().parse(SocialParserContext.builder(sender, Component.text(Social.get().getConfig().getChat().getReplyHoverText()))
                    .channel(reply.channel())
                    .build())
            );

        // Set icon
        replyIcon = Component.text(Social.get().getConfig().getChat().getReplyIcon())
            .hoverEvent(hoverText)
            .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "))
            .appendSpace()
            .append(Social.get().getTextProcessor().parse(SocialParserContext.builder(sender, Component.text(Social.get().getConfig().getChat().getReplyDescriptor()))
                .injectValue("reply_id", reply.id(), SocialInjectionParsers.PLACEHOLDER)
                .injectValue("reply_sender", reply.sender().displayName(), SocialInjectionParsers.PLACEHOLDER)
                .build()
            ))
            .appendSpace();

        return replyIcon;
    }

    public Component trim(final @NotNull Component component) {
        if (component instanceof TextComponent textComponent) {
            textComponent = textComponent.content(textComponent.content().trim());
            return textComponent;
        }

        return component;
    }

}
