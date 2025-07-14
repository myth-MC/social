package ovh.mythmc.social.api.chat.renderer;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@UtilityClass
public class SocialChatRendererUtil {

    public @NotNull TextComponent getReplyIcon(AbstractSocialUser sender, SocialRegisteredMessageContext message) {
        TextComponent replyIcon = Component.empty();

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
        replyIcon = (TextComponent) Component.text(Social.get().getConfig().getChat().getReplyIcon())
            .hoverEvent(hoverText)
            .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "))
            .appendSpace()
            .append(Social.get().getTextProcessor().parse(SocialParserContext.builder(sender, Component.text(Social.get().getConfig().getChat().getReplyDescriptor()))
                .injectValue(SocialInjectedValue.placeholder("reply_id", Component.text(reply.id())))
                .injectValue(SocialInjectedValue.placeholder("reply_sender", reply.sender().displayName()))
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
