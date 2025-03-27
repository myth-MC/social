package ovh.mythmc.social.bukkit.callback.game.invoker;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.network.channel.AbstractNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.C2SNetworkChannelWrapper;
import ovh.mythmc.social.api.network.channel.channels.SocialPayloadChannels;
import ovh.mythmc.social.api.network.payload.encoding.SocialPayloadEncoder;
import ovh.mythmc.social.common.callback.game.CustomPayloadReceive;
import ovh.mythmc.social.common.callback.game.CustomPayloadReceiveCallback;

public class CustomPayloadReceiveInvoker implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channelName, @NotNull Player player, byte[] message) {
        final var user = BukkitSocialUser.from(player);
        if (user == null)
            return;

        final var optionalChannel = SocialPayloadChannels.getByNamespacedString(channelName);

        optionalChannel
            .filter(channel -> channel instanceof C2SNetworkChannelWrapper<?>)
            .map(channel -> (AbstractNetworkChannelWrapper.C2S<?>) channel)
            .ifPresent(channel -> {
                final var decoded = channel.decode(SocialPayloadEncoder.of(message));
                CustomPayloadReceiveCallback.INSTANCE.invoke(new CustomPayloadReceive(user, channel, decoded));
            });
    }
    
}
