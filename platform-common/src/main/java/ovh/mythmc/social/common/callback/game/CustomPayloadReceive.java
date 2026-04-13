package ovh.mythmc.social.common.callback.game;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.callbacks.annotations.v1.Callback;
import ovh.mythmc.callbacks.annotations.v1.CallbackField;
import ovh.mythmc.callbacks.annotations.v1.CallbackFields;
import ovh.mythmc.social.api.network.channel.C2SNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.user.SocialUser;

@Callback
@CallbackFields({
        @CallbackField(field = "user", getter = "user()"),
        @CallbackField(field = "channel", getter = "channel()"),
        @CallbackField(field = "payload", getter = "payload()")
})
public final class CustomPayloadReceive {

    private final SocialUser user;
    private final C2SNetworkChannelWrapper<?> channel;
    private final NetworkPayloadWrapper.ClientToServer payload;

    public CustomPayloadReceive(
        @NotNull SocialUser user,
        @NotNull C2SNetworkChannelWrapper<?> channel,
        @NotNull NetworkPayloadWrapper.ClientToServer payload
    ) {
        this.user = user;
        this.channel = channel;
        this.payload = payload;
    }

    public @NotNull SocialUser user() {
        return this.user;
    }

    public @NotNull C2SNetworkChannelWrapper<?> channel() {
        return this.channel;
    }

    public @NotNull NetworkPayloadWrapper.ClientToServer payload() {
        return this.payload;
    }

}
