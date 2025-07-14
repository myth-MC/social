package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelClosePayload;

public final class SocialChannelCloseChannel extends AbstractNetworkChannelWrapper.S2C<SocialChannelClosePayload> {

    SocialChannelCloseChannel() {
        super(NetworkChannelIdentifiers.Channel.CLOSE);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialChannelClosePayload payload) {
        return SocialPayloadEncoder.of(payload.channel().name());
    }

}
