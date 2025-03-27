package ovh.mythmc.social.api.network.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public interface S2CNetworkChannelWrapper<E extends NetworkPayloadWrapper.ServerToClient> extends NetworkChannelWrapper {

    @NotNull SocialPayloadEncoder encode(final @NotNull E payload);

}
