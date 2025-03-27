package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.NetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifier;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class SocialPayloadChannels {

    public static final SocialBonjourChannel BONJOUR = new SocialBonjourChannel();

    public static final SocialChannelCloseAllChannel CLOSE_ALL_CHANNELS = new SocialChannelCloseAllChannel();

    public static final SocialChannelCloseChannel CLOSE_CHANNEL = new SocialChannelCloseChannel();

    public static final SocialChannelMentionChannel MENTION = new SocialChannelMentionChannel();

    public static final SocialChannelOpenChannel OPEN_CHANNEL = new SocialChannelOpenChannel();

    public static final SocialChannelsRefreshChannel REFRESH_CHANNELS = new SocialChannelsRefreshChannel();

    public static final SocialChannelSwitchChannel SWITCH_CHANNEL = new SocialChannelSwitchChannel();

    public static final SocialMessagePreviewChannel MESSAGE_PREVIEW = new SocialMessagePreviewChannel();

    private static final Collection<NetworkChannelWrapper> channels = List.of(
        BONJOUR,
        CLOSE_ALL_CHANNELS,
        CLOSE_CHANNEL,
        MENTION,
        OPEN_CHANNEL,
        REFRESH_CHANNELS,
        SWITCH_CHANNEL,
        MESSAGE_PREVIEW
    );

    public static Iterable<NetworkChannelWrapper> getAll() {
        return List.copyOf(channels);
    }

    public static Optional<NetworkChannelWrapper> getByIdentifier(final @NotNull NetworkChannelIdentifier identifier) {
        return channels.stream()
            .filter(channel -> channel.identifier().equals(identifier))
            .findAny();
    }

    public static Optional<NetworkChannelWrapper> getByNamespacedString(final @NotNull String namespacedString) {
        final String namespace = namespacedString.substring(0, namespacedString.indexOf(":"));
        final String identifier = namespacedString.substring(namespacedString.indexOf(":") + 1);

        final NetworkChannelIdentifier channelIdentifier = NetworkChannelIdentifier.of(namespace, identifier);
        return getByIdentifier(channelIdentifier);
    }

}
