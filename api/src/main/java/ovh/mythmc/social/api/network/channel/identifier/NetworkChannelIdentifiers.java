package ovh.mythmc.social.api.network.channel.identifier;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public abstract class NetworkChannelIdentifiers {

    public static final class Channel implements Iterable<NetworkChannelIdentifier> {

        public static final NetworkChannelIdentifier CLOSE_ALL = NetworkChannelIdentifier.of("closeall");

        public static final NetworkChannelIdentifier CLOSE = NetworkChannelIdentifier.of("close");

        public static final NetworkChannelIdentifier MENTION = NetworkChannelIdentifier.of("mention");

        public static final NetworkChannelIdentifier OPEN = NetworkChannelIdentifier.of("open");

        public static final NetworkChannelIdentifier REFRESH = NetworkChannelIdentifier.of("refresh");

        public static final NetworkChannelIdentifier SWITCH = NetworkChannelIdentifier.of("switch");

        private Channel() {
        }

        @Override
        public @NotNull Iterator<NetworkChannelIdentifier> iterator() {
            return List.of(
                CLOSE_ALL,
                CLOSE,
                MENTION,
                OPEN,
                REFRESH,
                SWITCH
            ).iterator();
        }

    }

    public static final class Message implements Iterable<NetworkChannelIdentifier> {

        public static final NetworkChannelIdentifier PREVIEW = NetworkChannelIdentifier.of("preview");

        private Message() {
        }

        @Override
        public @NotNull Iterator<NetworkChannelIdentifier> iterator() {
            return List.of(
                PREVIEW
            ).iterator();
        }

    }

    public static final class Other implements Iterable<NetworkChannelIdentifier> {

        public static final NetworkChannelIdentifier BONJOUR = NetworkChannelIdentifier.of("bonjour");

        private Other() {
        }

        @Override
        public @NotNull Iterator<NetworkChannelIdentifier> iterator() {
            return List.of(
                BONJOUR
            ).iterator();
        }

    }

}
