package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelMentionPayload;

public final class SocialChannelMentionChannel extends AbstractNetworkChannelWrapper.S2C<SocialChannelMentionPayload> {

    SocialChannelMentionChannel() {
        super(NetworkChannelIdentifiers.Channel.MENTION);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialChannelMentionPayload payload) {
        return SocialPayloadEncoder.of(
            payload.channel().name(),
            payload.sender().cachedDisplayName().get(),
            payload.sender().uuid().toString()
        );
    }

}
