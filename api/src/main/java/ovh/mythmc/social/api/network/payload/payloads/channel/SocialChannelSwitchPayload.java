package ovh.mythmc.social.api.network.payload.payloads.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.network.payload.AbstractNetworkPayloadWrapper;

public final class SocialChannelSwitchPayload extends AbstractNetworkPayloadWrapper.Bidirectional {

    private final ChatChannel channel;

    public SocialChannelSwitchPayload(final @NotNull ChatChannel channel) {
        this.channel = channel;
    }

    public ChatChannel channel() {
        return this.channel;
    }

}
