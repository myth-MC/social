package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.context.SocialMessageContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An in-memory store of all chat messages processed during the current server
 * session.
 *
 * <p>
 * Messages are addressable by their auto-assigned integer ID and can be looked
 * up
 * by channel, sender, or content fragment. The history is accessed via
 * {@link ovh.mythmc.social.api.Social#getChatManager() ChatManager#history}.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialRegisteredMessageContext> messages = new HashMap<>();

    /**
     * Records a new message in the history.
     *
     * @param messageContext the pre-processed message context
     * @param finalMessage   the fully rendered component that was sent to players
     * @return the newly created registered context, including its assigned ID
     */
    public SocialRegisteredMessageContext register(final @NotNull SocialMessageContext messageContext,
            final @NotNull Component finalMessage) {
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
    public SocialRegisteredMessageContext getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    /**
     * Returns {@code true} if the message can be deleted from the client due to
     * having a signed message attached.
     *
     * @param context the message context to check
     * @return {@code true} if deletion is possible
     */
    public boolean canDelete(SocialMessageContext context) {
        return context.signedMessage().isPresent() && context.signedMessage().get().canDelete();
    }

    /**
     * Marks a message as deleted for all online players and replaces its stored
     * component
     * with a red {@code "N/A"} placeholder.
     *
     * @param context the message to delete
     */
    public void delete(SocialRegisteredMessageContext context) {
        Social.get().getUserService().get().forEach(user -> user.deleteMessage(context.signedMessage().get()));

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
     *
     * @return an unordered list of all stored messages
     */
    public List<SocialRegisteredMessageContext> get() {
        return messages.values().stream().toList();
    }

    /**
     * Returns all messages sent in a specific channel.
     *
     * @param channel the channel to filter by
     * @return the messages sent in that channel
     */
    public List<SocialRegisteredMessageContext> getByChannel(final @NotNull ChatChannel channel) {
        return messages.values().stream()
                .filter(message -> message.channel().equals(channel))
                .toList();
    }

    /**
     * Returns all messages sent by the given user.
     *
     * @param user the sender to filter by
     * @return the messages sent by that user
     */
    public List<SocialRegisteredMessageContext> getByUser(final @NotNull SocialUser user) {
        return messages.values().stream()
                .filter(message -> message.sender().uuid().equals(user.uuid()))
                .toList();
    }

    /**
     * Returns all messages whose raw text contains the given substring.
     *
     * @param text the substring to search for
     * @return the matching messages
     */
    public List<SocialRegisteredMessageContext> getByText(final @NotNull String text) {
        return messages.values().stream()
                .filter(message -> message.rawMessage().contains(text))
                .toList();
    }

    /**
     * Returns all messages belonging to the same thread as the given message, up to
     * an upper bound.
     *
     * <p>
     * A thread includes the root message, all direct replies, and any messages that
     * share the same reply ID.
     *
     * @param messageContext the message whose thread should be retrieved
     * @param limit          the maximum number of results
     * @return the thread messages
     */
    public List<SocialRegisteredMessageContext> getThread(final @NotNull SocialRegisteredMessageContext messageContext,
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
