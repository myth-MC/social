package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents a registered and persisted social message.
 *
 * <p>
 * {@code SocialRegisteredMessageContext} extends {@link SocialMessageContext}
 * and adds metadata that becomes available once a message has been stored
 * in the chat history system.
 * </p>
 *
 * <p>
 * In addition to the base message context, this class provides:
 * </p>
 * <ul>
 *     <li>A unique message identifier</li>
 *     <li>The fully processed {@link Component} message</li>
 *     <li>The message creation timestamp</li>
 * </ul>
 *
 * <p>
 * Instances of this class typically represent messages that have been
 * registered in the chat history and can therefore be referenced (e.g. replies).
 * </p>
 *
 * @see SocialMessageContext
 * @see SocialUser
 * @see ChatChannel
 */
@Getter
@Accessors(fluent = true)
public class SocialRegisteredMessageContext extends SocialMessageContext {

    /**
     * The unique identifier of this message within the chat history.
     */
    private final Integer id;

    /**
     * The fully processed and formatted message component.
     */
    private final Component message;

    /**
     * The timestamp (in milliseconds since epoch) when this message was created.
     */
    private final long timestamp;

    /**
     * Constructs a new registered message context.
     *
     * @param id            the unique message ID
     * @param timestamp     the creation timestamp in milliseconds since epoch
     * @param sender        the message sender
     * @param channel       the channel the message was sent in
     * @param viewers       the audiences who received the message
     * @param message       the processed message component
     * @param rawMessage    the raw, unprocessed message content
     * @param replyId       the ID of the replied message, or {@code null}
     * @param signedMessage the signed message payload, or {@code null}
     */
    public SocialRegisteredMessageContext(
            int id,
            long timestamp,
            SocialUser sender,
            ChatChannel channel,
            Set<Audience> viewers,
            Component message,
            String rawMessage,
            Integer replyId,
            SignedMessage signedMessage) {

        super(sender, channel, viewers, rawMessage, replyId, signedMessage);

        this.timestamp = timestamp;
        this.id = id;
        this.message = message;
    }

    /**
     * Formats the message timestamp into a human-readable date string.
     *
     * <p>
     * The format pattern is retrieved from the configuration:
     * {@code Social.get().getConfig().getGeneral().getDateFormat()}.
     * </p>
     *
     * @return the formatted date string
     */
    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getGeneral().getDateFormat());
        return dateFormat.format(new Date(timestamp));
    }

}
