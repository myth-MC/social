package ovh.mythmc.social.api.network.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;

public interface C2SNetworkChannelWrapper<D extends NetworkPayloadWrapper.ClientToServer> extends NetworkChannelWrapper {

    @NotNull D decode(final @NotNull SocialPayloadEncoder encoder);

}
