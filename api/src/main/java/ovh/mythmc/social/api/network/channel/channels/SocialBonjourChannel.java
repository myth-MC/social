package ovh.mythmc.social.api.network.channel.channels;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.identifier.NetworkChannelIdentifiers;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.api.network.payload.payloads.SocialBonjourPayload;

public final class SocialBonjourChannel extends AbstractNetworkChannelWrapper.C2S<SocialBonjourPayload> {

    SocialBonjourChannel() {
        super(NetworkChannelIdentifiers.Other.BONJOUR);
    }

    @Override
    public @NotNull SocialBonjourPayload decode(@NotNull SocialPayloadEncoder encoder) {
        return new SocialBonjourPayload();
    }

}
