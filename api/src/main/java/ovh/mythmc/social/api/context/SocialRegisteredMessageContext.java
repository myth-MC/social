package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents a registered and persisted social message.
 */
public class SocialRegisteredMessageContext extends SocialMessageContext {

    private final Integer id;
    private final Component message;
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
            @NotNull SocialUser sender,
            @NotNull ChatChannel channel,
            @NotNull Set<Audience> viewers,
            @NotNull Component message,
            @NotNull String rawMessage,
            @Nullable Integer replyId,
            @Nullable SignedMessage signedMessage) {

        super(sender, channel, viewers, rawMessage, replyId, signedMessage);

        this.timestamp = timestamp;
        this.id = id;
        this.message = message;
    }

    /**
     * Returns the unique identifier of this message within the chat history.
     */
    public @NotNull Integer id() {
        return id;
    }

    /**
     * Returns the fully processed and formatted message component.
     */
    public @NotNull Component message() {
        return message;
    }

    /**
     * Returns the timestamp (in milliseconds since epoch) when this message was created.
     */
    public long timestamp() {
        return timestamp;
    }

    /**
     * Formats the message timestamp into a human-readable date string.
     *
     * @return the formatted date string
     */
    public @NotNull String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getGeneral().getDateFormat());
        return dateFormat.format(new Date(timestamp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialRegisteredMessageContext that)) return false;
        if (!super.equals(o)) return false;
        return timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, message, timestamp);
    }

    @Override
    public String toString() {
        return "SocialRegisteredMessageContext{" +
                "id=" + id +
                ", sender=" + sender() +
                ", timestamp=" + timestamp +
                '}';
    }
}

