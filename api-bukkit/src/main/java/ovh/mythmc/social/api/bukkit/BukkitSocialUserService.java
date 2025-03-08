package ovh.mythmc.social.api.bukkit;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitSocialUserService extends SocialUserService {

    public static final BukkitSocialUserService instance = new BukkitSocialUserService();

    @Override
    public Collection<AbstractSocialUser> get() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> getByUuid(player.getUniqueId()).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }
    
    @Override
    protected AbstractSocialUser createUserInstance(@NotNull UUID uuid) {
        String name = "";
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            name = player.getName();

        return new BukkitSocialUser(uuid, name);
    }

    
}
