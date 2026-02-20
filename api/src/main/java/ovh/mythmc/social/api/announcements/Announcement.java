package ovh.mythmc.social.api.announcements;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.configuration.section.settings.AnnouncementsSettings;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.Arrays;

/**
 * Represents a broadcast announcement that is sent to one or more
 * {@link ChatChannel channels}.
 *
 * <p>
 * Create instances via the static factory methods
 * {@link #of(Component, Iterable)}
 * or {@link #of(Component, ChatChannel...)}.
 */
public interface Announcement {

    /**
     * Creates a new announcement targeting the given channels.
     *
     * @param message  the message to broadcast
     * @param channels the channels to send the message to
     * @return a new announcement instance
     */
    static Announcement of(@NotNull Component message, @NotNull Iterable<ChatChannel> channels) {
        return new AnnouncementImpl(channels, message);
    }

    /**
     * Creates a new announcement targeting the given channels.
     *
     * @param message  the message to broadcast
     * @param channels the channels to send the message to
     * @return a new announcement instance
     */
    static Announcement of(@NotNull Component message, @NotNull ChatChannel... channels) {
        return new AnnouncementImpl(Arrays.asList(channels), message);
    }

    /**
     * Creates an announcement from its configuration-file representation.
     *
     * @param announcementField the config section representing this announcement
     * @return a new announcement instance
     */
    static Announcement fromConfigField(AnnouncementsSettings.Announcement announcementField) {
        return of(
                Component.text(announcementField.message()),
                announcementField.channels().stream()
                        .map(channelName -> Social.registries().channels().value(RegistryKey.identified(channelName))
                                .orElse(null))
                        .toList());
    }

    /**
     * Returns the channels this announcement will be broadcast to.
     *
     * @return the target channels
     */
    @NotNull
    Iterable<ChatChannel> channels();

    /**
     * Returns the message component that will be sent.
     *
     * @return the announcement message
     */
    @NotNull
    Component message();

}
