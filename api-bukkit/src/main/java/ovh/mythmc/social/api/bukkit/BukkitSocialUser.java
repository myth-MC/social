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

/**
 * The {@link SocialUser} implementation for the Bukkit platform.
 */
public class BukkitSocialUser extends AbstractSocialUser implements InGameSocialUser {

    /**
     * Gets the {@link BukkitSocialUser} instance for a specific {@link Player}.
     * @param player the {@link Player} to get the {@link BukkitSocialUser} instance from
     * @return       the {@link BukkitSocialUser} instance matching the {@link Player}
     */
    public static BukkitSocialUser from(@NotNull Player player) {
        return from(player.getUniqueId());
    }

    /**
     * Gets the {@link BukkitSocialUser} instance for a specific {@link SocialUser}.
     * @param user the {@link SocialUser} to get the {@link BukkitSocialUser} from
     * @return     the {@link BukkitSocialUser} instance matching the {@link SocialUser}
     */
    public static BukkitSocialUser from(@NotNull SocialUser user) {
        if (user instanceof BukkitSocialUser bukkitSocialUser)
            return bukkitSocialUser;

        return null;
    }

    /**
     * Gets the {@link BukkitSocialUser} instance for a specific {@link UUID}.
     * @param uuid the {@link UUID} to get the {@link BukkitSocialUser} from
     * @return     the {@link BukkitSocialUser} instance matching the {@link UUID}
     */
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
