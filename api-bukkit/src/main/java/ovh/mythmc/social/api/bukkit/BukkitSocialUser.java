package ovh.mythmc.social.api.bukkit;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper.ServerToClient;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.InGameSocialUser;
import ovh.mythmc.social.api.user.SocialUser;

public class BukkitSocialUser extends AbstractSocialUser implements InGameSocialUser {

    public static BukkitSocialUser from(@NotNull Player player) {
        return from(player.getUniqueId());
    }

    public static BukkitSocialUser from(@NotNull SocialUser user) {
        if (user instanceof BukkitSocialUser bukkitSocialUser)
            return bukkitSocialUser;

        return null;
    }

    public static BukkitSocialUser from(@NotNull UUID uuid) {
        assert uuid != null;
        return BukkitSocialUserService.INSTANCE.getOrCreate(uuid);
    }

    private final OfflinePlayer offlinePlayer;

    protected BukkitSocialUser(@NotNull Player player) {
        super(player.getUniqueId(), player.getName(), BukkitSocialUser.class);
        this.offlinePlayer = player;
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        if (!isOnline())
            return false;

        return player().get().hasPermission(permission);
    }

    @Override
    public <T extends ServerToClient> void sendCustomPayload(@NotNull S2CNetworkChannelWrapper<T> channel,
            @NotNull T payload) {
        player().ifPresent(player -> {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

            player.sendPluginMessage(
                    plugin,
                    channel.identifier().toString(),
                    channel.encode(payload).bytes());
        });
    }

    @Override
    public boolean isOnline() {
        return this.offlinePlayer.isOnline();
    }

    @Override
    public @NotNull Audience audience() {
        return SocialAdventureProvider.get().user(this);
    }

    public @NotNull OfflinePlayer offlinePlayer() {
        return this.offlinePlayer;
    }

    public @NotNull Optional<Player> player() {
        return Optional.ofNullable(this.offlinePlayer.getPlayer());
    }

}
