package ovh.mythmc.social.bukkit.callback.game.invoker;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.common.callback.game.CustomPayloadReceive;
import ovh.mythmc.social.common.callback.game.CustomPayloadReceiveCallback;

public class CustomPayloadReceiveInvoker implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        final var user = BukkitSocialUser.from(player);
        if (user == null)
            return;

        CustomPayloadReceiveCallback.INSTANCE.invoke(new CustomPayloadReceive(user, channel, message));
    }
    
}
