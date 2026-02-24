package ovh.mythmc.social.api.bukkit;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.identity.IdentityResolver;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.user.SocialUserService;

public class BukkitSocialUserService implements SocialUserService {

    public static final BukkitSocialUserService INSTANCE = new BukkitSocialUserService();

    private BukkitSocialUserService() {
    }

    private final BukkitIdentityResolver identityResolver = new BukkitIdentityResolver();
    private final Map<UUID, BukkitSocialUser> userMap = new ConcurrentHashMap<>();

    @Override
    public @NotNull IdentityResolver identityResolver() {
        return this.identityResolver;
    }

    @Override
    public @NotNull Set<SocialUser> get() {
        return userMap.values().stream().collect(Collectors.toSet());
    }

    @Override
    public @NotNull BukkitSocialUser getOrCreate(@NotNull UUID uuid) {
        return userMap.computeIfAbsent(uuid, userUuid -> {
            final Player player = Bukkit.getPlayer(uuid);
            return new BukkitSocialUser(player);
        });
    }

    @Override
    public @NotNull Optional<SocialUser> getByUuid(@NotNull UUID uuid) {
        return Optional.ofNullable(userMap.get(uuid));
    }

}
