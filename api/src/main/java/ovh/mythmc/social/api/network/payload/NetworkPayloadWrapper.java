package ovh.mythmc.social.api.network.payload;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface NetworkPayloadWrapper {

    interface ServerToClient extends NetworkPayloadWrapper {

    }

    interface ClientToServer extends NetworkPayloadWrapper {

    }

}
