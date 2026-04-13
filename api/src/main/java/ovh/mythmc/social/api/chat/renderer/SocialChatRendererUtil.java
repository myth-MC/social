package ovh.mythmc.social.api.chat.renderer;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Utility methods for rendering chat components in the chat system.
 * <p>
 * Provides methods for constructing icons, hover text, and performing lightweight
 * {@link Component} transformations for chat messages.
 *
 * <p>Example usage:
 * <pre>{@code
 * TextComponent replyIcon = SocialChatRendererUtil.getReplyIcon(sender, messageContext);
 * Component trimmedMessage = SocialChatRendererUtil.trim(originalMessage);
 * }</pre>
 */
public final class SocialChatRendererUtil {

    private SocialChatRendererUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns a reply icon component for a message, including hover text with thread context
     * and a click event to suggest replying.
     *
     * <p>The component will be empty if:
     * <ul>
     *     <li>The message is not a reply, or</li>
     *     <li>The sender does not have permission to send messages in the channel</li>
     * </ul>
     *
     * <p>The hover text will include:
     * <ul>
     *     <li>Messages in the thread (up to 8 recent replies)</li>
     *     <li>Ellipsis (...) if more than 8 replies exist</li>
     *     <li>A "click to reply" instruction from the chat configuration</li>
     * </ul>
     *
     * <p>The returned {@link TextComponent} includes:
     * <ul>
     *     <li>The configured reply icon</li>
     *     <li>Hover text with context</li>
     *     <li>Click event suggesting the reply command for this message</li>
     *     <li>Optional descriptor text from configuration</li>
     * </ul>
     *
     * @param sender  the {@link SocialUser} who will see the icon
     * @param message the {@link SocialRegisteredMessageContext} representing the message
     * @return a {@link TextComponent} representing the reply icon, or empty if unavailable
     */
    public static @NotNull TextComponent getReplyIcon(SocialUser sender, SocialRegisteredMessageContext message) {
        TextComponent replyIcon = Component.empty();

        // Check that message is a reply
        if (!message.isReply())
            return replyIcon;

        // Check that sender has permission to send messages on this channel
        if (!Social.get().getChatManager().hasPermission(sender, message.channel()))
            return replyIcon;

        // Get the reply context
        final SocialRegisteredMessageContext reply = Social.get().getChatManager().getHistory()
                .getById(message.replyId());

        // Icon hover text
        Component hoverText = Component.empty();
        for (SocialRegisteredMessageContext threadReply : Social.get().getChatManager().getHistory().getThread(reply,
                8)) {
            hoverText = hoverText
                    .append(threadReply.sender().displayNameOrUsername())
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
                        Social.get().getTextProcessor().parse(SocialParserContext
                                .builder(sender, Component.text(Social.get().getConfig().getChat().getReplyHoverText()))
                                .channel(reply.channel())
                                .build()));

        // Set icon
        replyIcon = (TextComponent) Component.text(Social.get().getConfig().getChat().getReplyIcon())
                .hoverEvent(hoverText)
                .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "))
                .appendSpace()
                .append(Social.get().getTextProcessor().parse(SocialParserContext
                        .builder(sender, Component.text(Social.get().getConfig().getChat().getReplyDescriptor()))
                        .injectValue(SocialInjectedValue.placeholder("reply_id", Component.text(reply.id())))
                        .injectValue(
                                SocialInjectedValue.placeholder("reply_sender", reply.sender().displayNameOrUsername()))
                        .build()))
                .appendSpace();

        return replyIcon;
    }

    /**
     * Trims the leading and trailing whitespace of a {@link Component}'s textual content.
     *
     * <p>If the component is a {@link TextComponent}, its content is trimmed; otherwise,
     * the component is returned unchanged.
     *
     * @param component the {@link Component} to trim
     * @return the trimmed component if textual, or the original component otherwise
     */
    public static Component trim(final @NotNull Component component) {
        if (component instanceof TextComponent textComponent) {
            textComponent = textComponent.content(textComponent.content().trim());
            return textComponent;
        }

        return component;
    }

}

