package ovh.mythmc.social.api.context;

import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents the rendering context of a message.
 */
public class SocialRendererContext implements SocialContext {

    private final SocialUser sender;
    private final ChatChannel channel;
    private final Set<Audience> viewers;
    private final Component prefix;
    private final String plainMessage;
    private final Component message;
    private final Integer replyId;
    private final int messageId;

    /**
     * Constructs a new {@link SocialRendererContext}.
     *
     * @param sender       the user who sent the message
     * @param channel      the channel in which the message was sent
     * @param viewers      the audiences who will receive the rendered message
     * @param prefix       the prefix component applied before the message content
     * @param plainMessage the plain, unformatted message string
     * @param message      the fully formatted message component
     * @param replyId      the ID of the message being replied to, or {@code null}
     * @param messageId    the unique identifier of this message
     */
    public SocialRendererContext(
            @NotNull SocialUser sender,
            @NotNull ChatChannel channel,
            @NotNull Set<Audience> viewers,
            @NotNull Component prefix,
            @NotNull String plainMessage,
            @NotNull Component message,
            @Nullable Integer replyId,
            int messageId) {
        this.sender = sender;
        this.channel = channel;
        this.viewers = viewers;
        this.prefix = prefix;
        this.plainMessage = plainMessage;
        this.message = message;
        this.replyId = replyId;
        this.messageId = messageId;
    }

    /**
     * Returns the user who sent the message.
     */
    public @NotNull SocialUser sender() {
        return sender;
    }

    /**
     * Returns the channel in which the message was sent.
     */
    public @NotNull ChatChannel channel() {
        return channel;
    }

    /**
     * Returns the audiences who will receive the rendered message.
     */
    public @NotNull Set<Audience> viewers() {
        return viewers;
    }

    /**
     * Returns the prefix component applied before the message content.
     */
    public @NotNull Component prefix() {
        return prefix;
    }

    /**
     * Returns the plain, unformatted message string.
     */
    public @NotNull String plainMessage() {
        return plainMessage;
    }

    /**
     * Returns the fully formatted message component.
     */
    public @NotNull Component message() {
        return message;
    }

    /**
     * Returns the ID of the message being replied to, or {@code null} if not a reply.
     */
    public @Nullable Integer replyId() {
        return replyId;
    }

    /**
     * Returns the unique identifier of this message.
     */
    public int messageId() {
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SocialRendererContext that)) return false;
        return messageId == that.messageId &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(viewers, that.viewers) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(plainMessage, that.plainMessage) &&
                Objects.equals(message, that.message) &&
                Objects.equals(replyId, that.replyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, channel, viewers, prefix, plainMessage, message, replyId, messageId);
    }

    @Override
    public String toString() {
        return "SocialRendererContext{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", channel=" + channel +
                '}';
    }
}

