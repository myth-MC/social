package ovh.mythmc.social.api.network.payload.payloads.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.network.payload.AbstractNetworkPayloadWrapper;

public final class SocialChannelClosePayload extends AbstractNetworkPayloadWrapper.S2C {

    private final ChatChannel channel;

    public SocialChannelClosePayload(final @NotNull ChatChannel channel) {
        this.channel = channel;
    }

    public ChatChannel channel() {
        return this.channel;
    }

}
