package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.parser.SocialPayloadComponentParsers;
import ovh.mythmc.social.api.network.payload.payloads.channel.SocialChannelSwitchPayload;

public final class SocialChannelSwitchChannel extends AbstractNetworkChannelWrapper.Bidirectional<SocialChannelSwitchPayload, SocialChannelSwitchPayload> {

    SocialChannelSwitchChannel() {
        super(NetworkChannelIdentifiers.Channel.SWITCH);
    }

    @Override
    public @NotNull SocialChannelSwitchPayload decode(@NotNull SocialPayloadEncoder encoder) {
        final ChatChannel chatChannel = SocialPayloadComponentParsers.channel().parse(encoder);
        return new SocialChannelSwitchPayload(chatChannel);
    }

    @Override
    public @NotNull SocialPayloadEncoder encode(@NotNull SocialChannelSwitchPayload payload) {
        return SocialPayloadEncoder.of(payload.channel().name());
    }

}
