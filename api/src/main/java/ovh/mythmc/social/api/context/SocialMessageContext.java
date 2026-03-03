package ovh.mythmc.social.api.context;

import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
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
@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialMessageContext implements SocialContext {

    /**
     * The user who sent the message.
     */
    private final SocialUser sender;

    /**
     * The chat channel the message was sent in.
     */
    private final ChatChannel channel;

    /**
     * The audiences that should receive the message.
     */
    private final Set<Audience> viewers;

    /**
     * The raw, unprocessed message content.
     */
    private final String rawMessage;

    /**
     * The ID of the message being replied to, if this message is a reply.
     * May be {@code null} if this message is not a reply.
     */
    private final Integer replyId;

    /**
     * The signed message payload, if available.
     * May be {@code null} when the message is not signed or
     * when signing is not supported.
     */
    private final @Nullable SignedMessage signedMessage;

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

}