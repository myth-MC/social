package ovh.mythmc.social.api.bukkit;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.user.SocialUserService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitSocialUserService extends SocialUserService<Player, BukkitSocialUser> {

    protected static final BukkitSocialUserService instance = new BukkitSocialUserService();

    @Override
    protected BukkitSocialUser createInstance(@NotNull Player player, @NotNull UUID uuid) {
        return new BukkitSocialUser(uuid, player, player.getName());
    }

    @Override
    public boolean canMap(Object player) {
        return player instanceof Player;
    }

    @Override
    public BukkitSocialUser map(Object o) {
        if (o instanceof Player player)
            return getByUuid(player.getUniqueId()).orElse(null);

        return null;
    }
    
}
