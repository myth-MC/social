package ovh.mythmc.social.api.chat;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.util.Mutable;

/**
 * Represents a participant in the chat system.
 *
 * <p>A {@code ChatParticipant} can have a main chat channel, optionally belong to a group channel,
 * block certain channels, and track the last private message recipient. Some fields are
 * wrapped in {@link Mutable} to indicate that they can be modified safely by the system or API consumers.
 *
 * <p>Implementations of this interface should provide thread-safe access to mutable fields
 * where necessary.
 */
public interface ChatParticipant {

    /**
     * Gets the participant's main chat channel.
     *
     * <p>This is wrapped in {@link Mutable} so the main channel can be changed dynamically.
     *
     * @return a {@link Mutable} containing the current {@link ChatChannel}
     */
    @NotNull
    Mutable<ChatChannel> mainChannel();

    /**
     * Gets the group chat channel the participant is currently part of, if any.
     *
     * @return an {@link Optional} containing the {@link GroupChatChannel}, or empty if not in a group
     */
    @NotNull
    Optional<GroupChatChannel> groupChannel();

    /**
     * Returns the set of channel names that this participant has blocked.
     *
     * <p>Blocked channels are those whose messages the participant will not receive.
     *
     * @return an immutable {@link Set} of blocked channel IDs
     */
    @NotNull
    Set<String> blockedChannels();

    /**
     * Gets the last participant to whom this user sent a private message.
     *
     * <p>This is wrapped in {@link Mutable} so it can be updated whenever a private message is sent.
     *
     * @return a {@link Mutable} containing the {@link UUID} of the last private message recipient
     */
    @NotNull
    Mutable<UUID> lastPrivateMessageRecipient();

}
