package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelCloseAllPayload;

public final class SocialChannelCloseAllChannel extends AbstractNetworkChannelWrapper.S2C<SocialChannelCloseAllPayload> {

    SocialChannelCloseAllChannel() {
        super(NetworkChannelIdentifiers.Channel.CLOSE_ALL);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialChannelCloseAllPayload payload) {
        return SocialPayloadEncoder.empty();
    }

}
