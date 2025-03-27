package ovh.mythmc.social.api.network.payload.payloads.channel;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.network.payload.AbstractNetworkPayloadWrapper;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class SocialChannelMentionPayload extends AbstractNetworkPayloadWrapper.S2C {

    private final ChatChannel channel;

    private final AbstractSocialUser sender;

    public SocialChannelMentionPayload(final @NotNull ChatChannel channel, final @NotNull AbstractSocialUser sender) {
        this.channel = channel;
        this.sender = sender;
    }

    public ChatChannel channel() {
        return this.channel;
    }

    public AbstractSocialUser sender() {
        return this.sender;
    }

}
