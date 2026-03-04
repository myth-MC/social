package ovh.mythmc.social.api.network;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.user.SocialUserCompanion;

public interface CompanionClient {

    /**
     * Gets the optional {@link SocialUserCompanion} instance of this user.
     * @return an {@link Optional} containing the {@link SocialUserCompanion} instance
     *         of this user, or an empty {@link Optional} otherwise
     */
    @NotNull
    Optional<SocialUserCompanion> companion();

    /**
     * Sends a custom payload to this user.
     * @param <T>     the {@link NetworkPayloadWrapper.ServerToClient} implementation
     *                to send
     * @param channel the {@link S2CNetworkChannelWrapper} where the payload should be
     *                sent
     * @param payload the payload that should be sent
     */
    <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(
            @NotNull S2CNetworkChannelWrapper<T> channel,
            @NotNull T payload);

}
