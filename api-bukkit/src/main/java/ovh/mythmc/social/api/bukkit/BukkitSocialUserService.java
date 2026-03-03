package ovh.mythmc.social.api.bukkit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.identity.IdentityResolver;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.user.SocialUserService;

public class BukkitSocialUserService implements SocialUserService {

    public static final BukkitSocialUserService INSTANCE = new BukkitSocialUserService();

    private BukkitSocialUserService() {
        SocialScheduler.get().runAsyncTaskLater(() -> {
            int removed = clearCache();
            if (Social.get().getConfig().getGeneral().isDebug())
                Social.get().getLogger().info("{} users were cleared from the cache.", removed);
        }, 60 * 20); // every minute
    }

    private final BukkitIdentityResolver identityResolver = new BukkitIdentityResolver();
    private final Map<UUID, BukkitSocialUser> userMap = new ConcurrentHashMap<>();

    @Override
    public @NotNull IdentityResolver identityResolver() {
        return this.identityResolver;
    }

    @Override
    public @NotNull Set<SocialUser> get() {
        Set<SocialUser> resultSet = new HashSet<>();

        for (BukkitSocialUser user : userMap.values()) {
            if (!user.isExpired()) {
                resultSet.add(user);
            }
        }

        return Collections.unmodifiableSet(resultSet); 
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

    private int clearCache() {
        AtomicInteger removed = new AtomicInteger();

        userMap.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                removed.incrementAndGet();
                return true;
            }
            return false;
        });

        return removed.get();
    }

}
