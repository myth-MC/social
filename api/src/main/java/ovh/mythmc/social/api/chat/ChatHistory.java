package ovh.mythmc.social.api.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An in-memory store of all chat messages processed during the current server
 * session.
 */
public final class ChatHistory {

    private final Map<Integer, SocialRegisteredMessageContext> messages = new HashMap<>();

    ChatHistory() {}

    /**
     * Records a new message in the history.
     *
     * @param messageContext the pre-processed message context
     * @param finalMessage   the fully rendered component that was sent to players
     * @return the newly created registered context, including its assigned ID
     */
    public @NotNull SocialRegisteredMessageContext register(@NotNull SocialMessageContext messageContext,
                                                           @NotNull Component finalMessage) {
        int id = messages.size();

        SocialRegisteredMessageContext historyContext = new SocialRegisteredMessageContext(
                id,
                System.currentTimeMillis(),
                messageContext.sender(),
                messageContext.channel(),
                messageContext.viewers(),
                finalMessage,
                messageContext.rawMessage(),
                messageContext.replyId(),
                messageContext.signedMessage().orElse(null));

        messages.put(id, historyContext);
        return historyContext;
    }

    /**
     * Returns the message with the given ID, or {@code null} if not found.
     *
     * @param id the message ID
     * @return the registered context, or {@code null}
     */
    public @Nullable SocialRegisteredMessageContext getById(@NotNull Integer id) {
        return messages.get(id);
    }

    /**
     * Returns {@code true} if the message can be deleted from the client due to
     * having a signed message attached.
     *
     * @param context the message context to check
     * @return {@code true} if deletion is possible
     */
    public boolean canDelete(@NotNull SocialMessageContext context) {
        return context.signedMessage().isPresent() && context.signedMessage().get().canDelete();
    }

    /**
     * Marks a message as deleted for all online players and replaces its stored
     * component with a red {@code "N/A"} placeholder.
     *
     * @param context the message to delete
     */
    public void delete(@NotNull SocialRegisteredMessageContext context) {
        context.signedMessage().ifPresent(signed -> 
            Social.get().getUserService().get().forEach(user -> user.deleteMessage(signed))
        );

        SocialRegisteredMessageContext newContext = new SocialRegisteredMessageContext(
                context.id(),
                context.timestamp(),
                context.sender(),
                context.channel(),
                context.viewers(),
                Component.text("N/A", NamedTextColor.RED),
                context.rawMessage(),
                context.replyId(),
                context.signedMessage().orElse(null));

        messages.put(context.id(), newContext);
    }

    /**
     * Returns all messages in the history.
     */
    public @NotNull List<SocialRegisteredMessageContext> get() {
        return List.copyOf(messages.values());
    }

    /**
     * Returns all messages sent in a specific channel.
     */
    public @NotNull List<SocialRegisteredMessageContext> getByChannel(@NotNull ChatChannel channel) {
        return messages.values().stream()
                .filter(message -> message.channel().equals(channel))
                .toList();
    }

    /**
     * Returns all messages sent by the given user.
     */
    public @NotNull List<SocialRegisteredMessageContext> getByUser(@NotNull SocialUser user) {
        return messages.values().stream()
                .filter(message -> message.sender().uuid().equals(user.uuid()))
                .toList();
    }

    /**
     * Returns all messages whose raw text contains the given substring.
     */
    public @NotNull List<SocialRegisteredMessageContext> getByText(@NotNull String text) {
        return messages.values().stream()
                .filter(message -> message.rawMessage().contains(text))
                .toList();
    }

    /**
     * Returns all messages belonging to the same thread as the given message, up to
     * an upper bound.
     */
    public @NotNull List<SocialRegisteredMessageContext> getThread(@NotNull SocialRegisteredMessageContext messageContext,
            int limit) {
        return messages.values().stream()
                .filter(value -> (value.isReply() && Objects.equals(value.replyId(), messageContext.id())) ||
                        (value.isReply() && Objects.equals(value.replyId(), messageContext.replyId())) ||
                        value.id().equals(messageContext.id()) ||
                        value.id().equals(messageContext.replyId()))
                .limit(limit)
                .toList();
    }

}

