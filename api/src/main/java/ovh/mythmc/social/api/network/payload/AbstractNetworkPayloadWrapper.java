package ovh.mythmc.social.api.network.payload;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class AbstractNetworkPayloadWrapper implements NetworkPayloadWrapper {

    public static abstract class S2C extends AbstractNetworkPayloadWrapper implements NetworkPayloadWrapper.ServerToClient {

    }

    public static abstract class C2S extends AbstractNetworkPayloadWrapper implements NetworkPayloadWrapper.ClientToServer {

    }

    public static abstract class Bidirectional extends AbstractNetworkPayloadWrapper implements NetworkPayloadWrapper.ServerToClient, NetworkPayloadWrapper.ClientToServer {

    }

}
