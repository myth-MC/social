package ovh.mythmc.social.api.context;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents the context of a message sent through the social chat system.
 *
 * <p>
 * A {@code SocialMessageContext} contains all relevant information about a
 * message at the time it is processed, including:
 * </p>
 *
 * <ul>
 *     <li>The {@link SocialUser} who sent the message</li>
 *     <li>The {@link ChatChannel} the message was sent in</li>
 *     <li>The set of {@link Audience viewers} who can receive the message</li>
 *     <li>The raw, unformatted message content</li>
 *     <li>An optional reply message ID</li>
 *     <li>An optional {@link SignedMessage}</li>
 * </ul>
 *
 * @see SocialContext
 * @see SocialUser
 * @see ChatChannel
 * @see SignedMessage
 */
public class SocialMessageContext implements SocialContext {

    private final SocialUser sender;
    private final ChatChannel channel;
    private final Set<Audience> viewers;
    private final String rawMessage;
    private final Integer replyId;
    private final @Nullable SignedMessage signedMessage;

    /**
     * Constructs a new {@link SocialMessageContext}.
     *
     * @param sender        the user who sent the message
     * @param channel       the chat channel the message was sent in
     * @param viewers       the audiences that should receive the message
     * @param rawMessage    the raw, unprocessed message content
     * @param replyId       the ID of the message being replied to, if any
     * @param signedMessage the signed message payload, if available
     */
    public SocialMessageContext(
            @NotNull SocialUser sender,
            @NotNull ChatChannel channel,
            @NotNull Set<Audience> viewers,
            @NotNull String rawMessage,
            @Nullable Integer replyId,
            @Nullable SignedMessage signedMessage) {
        this.sender = sender;
        this.channel = channel;
        this.viewers = viewers;
        this.rawMessage = rawMessage;
        this.replyId = replyId;
        this.signedMessage = signedMessage;
    }

    /**
     * Returns the user who sent the message.
     *
     * @return the message sender
     */
    public @NotNull SocialUser sender() {
        return sender;
    }

    /**
     * Returns the chat channel the message was sent in.
     *
     * @return the chat channel
     */
    public @NotNull ChatChannel channel() {
        return channel;
    }

    /**
     * Returns the audiences that should receive the message.
     *
     * @return the set of audiences
     */
    public @NotNull Set<Audience> viewers() {
        return viewers;
    }

    /**
     * Returns the raw, unprocessed message content.
     *
     * @return the raw message string
     */
    public @NotNull String rawMessage() {
        return rawMessage;
    }

    /**
     * Returns the ID of the message being replied to, if this message is a reply.
     *
     * @return the reply ID, or {@code null} if not a reply
     */
    public @Nullable Integer replyId() {
        return replyId;
    }

    /**
     * Returns the signed message, if present.
     *
     * @return an {@link Optional} containing the {@link SignedMessage}
     *         if available, otherwise an empty {@link Optional}
     */
    public Optional<SignedMessage> signedMessage() {
        return Optional.ofNullable(signedMessage);
    }

    /**
     * Determines whether this message is a valid reply to another message.
     *
     * <p>
     * A message is considered a reply if:
     * </p>
     * <ul>
     *     <li>{@code replyId} is not {@code null}, and</li>
     *     <li>A message with that ID exists in the chat history</li>
     * </ul>
     *
     * @return {@code true} if this message references an existing message,
     *         {@code false} otherwise
     */
    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialMessageContext that)) return false;
        return Objects.equals(sender, that.sender) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(viewers, that.viewers) &&
                Objects.equals(rawMessage, that.rawMessage) &&
                Objects.equals(replyId, that.replyId) &&
                Objects.equals(signedMessage, that.signedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, channel, viewers, rawMessage, replyId, signedMessage);
    }

    @Override
    public String toString() {
        return "SocialMessageContext{" +
                "sender=" + sender +
                ", channel=" + channel +
                ", viewers=" + viewers +
                ", rawMessage='" + rawMessage + '\'' +
                ", replyId=" + replyId +
                ", signedMessage=" + signedMessage +
                '}';
    }
}