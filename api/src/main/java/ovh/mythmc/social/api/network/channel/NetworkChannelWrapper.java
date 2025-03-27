package ovh.mythmc.social.api.network.channel;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifier;

@ApiStatus.NonExtendable
public interface NetworkChannelWrapper {

    @NotNull Type type();

    @NotNull NetworkChannelIdentifier identifier();

    default boolean isBidirectional() {
        return this.type().equals(Type.BIDIRECTIONAL);
    }

    default boolean isClientToServer() {
        return this.type().equals(Type.CLIENT_TO_SERVER);
    }

    default boolean isServerToClient() {
        return this.type().equals(Type.SERVER_TO_CLIENT);
    }

    enum Type {

        BIDIRECTIONAL,
        CLIENT_TO_SERVER,
        SERVER_TO_CLIENT

    }

}