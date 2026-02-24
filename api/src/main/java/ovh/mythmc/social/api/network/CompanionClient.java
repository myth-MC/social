package ovh.mythmc.social.api.network;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.user.SocialUserCompanion;

public interface CompanionClient {

    @NotNull
    Optional<SocialUserCompanion> companion();

    <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(
            @NotNull S2CNetworkChannelWrapper<T> channel,
            @NotNull T payload);

}
