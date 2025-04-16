package ovh.mythmc.social.api.bukkit;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.network.channel.S2CNetworkChannelWrapper;
import ovh.mythmc.social.api.network.payload.NetworkPayloadWrapper;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.libs.com.j256.ormlite.table.DatabaseTable;

@Getter
@Accessors(fluent = true)
@DatabaseTable(tableName = "users")
public final class BukkitSocialUser extends AbstractSocialUser {

    BukkitSocialUser() {
    }

    BukkitSocialUser(UUID uuid) {
        super(uuid);
    }

    public static BukkitSocialUser from(AbstractSocialUser user) {
        if (user == null)
            return null;

        if (user instanceof BukkitSocialUser bukkitSocialUser)
            return bukkitSocialUser;

        return null;
    }

    public static BukkitSocialUser from(@NotNull Player player) {
        return from(player.getUniqueId());
    }

    public static BukkitSocialUser from(@NotNull UUID uuid) {
        return from(Social.get().getUserService().getByUuid(uuid).orElse(null));
    }

    @Override
    public Class<? extends SocialUser> rendererClass() {
        return BukkitSocialUser.class;
    }

    @Override
    public @NotNull Audience audience() {
        return SocialAdventureProvider.get().user(this);
    }

    @Override
    public String name() {
        return player().get().getName();
    }

    @Override
    public void name(@NotNull String name) {
        player().get().setDisplayName(name);
    }

    @Override
    public boolean checkPermission(@NotNull String permission) {
        return player().get().hasPermission(permission);
    }

    @Override
    public boolean isOnline() {
        return player().isPresent();
    }

    @Override
    public <T extends NetworkPayloadWrapper.ServerToClient> void sendCustomPayload(@NotNull S2CNetworkChannelWrapper<T> channel, @NotNull T payload) {
        player().ifPresent(player -> {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

            player.sendPluginMessage(
                plugin,
                channel.identifier().toString(),
                channel.encode(payload).bytes()
            );
        });
    }

    @Override
    public void playReaction(@NotNull Reaction reaction) {
        Social.get().getReactionFactory().play(this, reaction);
    }

    public Optional<Player> player() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
    
}
