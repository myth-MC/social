package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelsRefreshPayload;

public final class SocialChannelsRefreshChannel extends AbstractNetworkChannelWrapper.C2S<NetworkPayloadWrapper.ClientToServer> {

    SocialChannelsRefreshChannel() {
        super(NetworkChannelIdentifiers.Channel.REFRESH);
    }

    @Override
    public @NotNull SocialChannelsRefreshPayload decode(@NotNull SocialPayloadEncoder encoder) {
        return new SocialChannelsRefreshPayload();
    }

}
