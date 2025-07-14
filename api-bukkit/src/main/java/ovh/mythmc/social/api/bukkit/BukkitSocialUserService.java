package ovh.mythmc.social.api.bukkit;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.database.reference.UUIDResolver;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUserService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitSocialUserService extends SocialUserService {

    public static final BukkitSocialUserService instance = new BukkitSocialUserService();

    private final BukkitUUIDResolver uuidResolver = new BukkitUUIDResolver();

    @Override
    public UUIDResolver uuidResolver() {
        return this.uuidResolver;
    }

    @Override
    public Collection<AbstractSocialUser> get() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> getByUuid(player.getUniqueId()).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }
    
    @Override
    protected AbstractSocialUser createUserInstance(@NotNull UUID uuid) {
        return new BukkitSocialUser(uuid);
    }
    
}
