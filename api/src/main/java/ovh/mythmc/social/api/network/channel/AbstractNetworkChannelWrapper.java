package ovh.mythmc.social.api.network.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifier;

public final class AbstractNetworkChannelWrapper {

    static abstract class Abstract implements NetworkChannelWrapper {

        private final NetworkChannelIdentifier identifier;

        Abstract(final @NotNull NetworkChannelIdentifier identifier) {
            this.identifier = identifier;
        }

        @Override
        public @NotNull NetworkChannelIdentifier identifier() {
            return this.identifier;
        }

    }

    public static abstract class S2C<E extends NetworkPayloadWrapper.ServerToClient> extends Abstract implements S2CNetworkChannelWrapper<E> {

        @Override
        public @NotNull Type type() {
            return Type.SERVER_TO_CLIENT;
        }

        protected S2C(@NotNull NetworkChannelIdentifier identifier) {
            super(identifier);
        }

    }

    public static abstract class C2S<D extends NetworkPayloadWrapper.ClientToServer> extends Abstract implements C2SNetworkChannelWrapper<D> {

        @Override
        public @NotNull Type type() {
            return Type.CLIENT_TO_SERVER;
        }

        protected C2S(@NotNull NetworkChannelIdentifier identifier) {
            super(identifier);
        }

    }

    public static abstract class Bidirectional<E extends NetworkPayloadWrapper.ServerToClient, D extends NetworkPayloadWrapper.ClientToServer> extends Abstract implements S2CNetworkChannelWrapper<E>, C2SNetworkChannelWrapper<D> {

        @Override
        public @NotNull Type type() {
            return Type.BIDIRECTIONAL;
        }

        protected Bidirectional(@NotNull NetworkChannelIdentifier channel) {
            super(channel);
        }

    }

}
