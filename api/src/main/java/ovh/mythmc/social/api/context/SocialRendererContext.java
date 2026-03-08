package ovh.mythmc.social.api.context;

import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

/**
 * Represents the rendering context of a message.
 *
 * <p>
 * {@code SocialRendererContext} contains all information required during
 * the rendering phase of a message, where formatted components are assembled
 * and prepared for delivery to viewers.
 * </p>
 *
 * <p>
 * This context is typically used by rendering logic to build the final
 * {@link Component} shown to each {@link Audience}.
 * </p>
 *
 * <p>
 * It includes:
 * </p>
 * <ul>
 *     <li>The {@link SocialUser} who sent the message</li>
 *     <li>The {@link ChatChannel} the message belongs to</li>
 *     <li>The set of {@link Audience viewers} receiving the message</li>
 *     <li>The rendered prefix component</li>
 *     <li>The plain (unformatted) message content</li>
 *     <li>The fully formatted message component</li>
 *     <li>An optional reply message ID</li>
 *     <li>The unique message ID</li>
 * </ul>
 *
 * @see SocialContext
 * @see SocialUser
 * @see ChatChannel
 */
@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@RequiredArgsConstructor
public class SocialRendererContext implements SocialContext {

    /**
     * The user who sent the message.
     */
    private final SocialUser sender;

    /**
     * The channel in which the message was sent.
     */
    private final ChatChannel channel;

    /**
     * The audiences who will receive the rendered message.
     */
    private final Set<Audience> viewers;

    /**
     * The prefix component applied before the message content.
     */
    private final Component prefix;

    /**
     * The plain, unformatted message string.
     */
    private final String plainMessage;

    /**
     * The fully formatted message component.
     */
    private final Component message;

    /**
     * The ID of the message being replied to, or {@code null} if not a reply.
     */
    private final Integer replyId;

    /**
     * The unique identifier of this message.
     */
    private final int messageId;

}
