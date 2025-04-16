package ovh.mythmc.social.api.database.reference;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.scheduler.SocialScheduler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractUUIDResolver implements UUIDResolver {

    private final Set<OfflinePlayerReference> offlinePlayers = new HashSet<>();

    protected abstract Iterable<OfflinePlayerReference> serverOfflinePlayers();

    protected AbstractUUIDResolver() {
        // Once the platform is initialized, we can proceed to cache the offline player references
        SocialScheduler.onPlatformInit(() -> {
            SocialScheduler.get().runAsyncTask(() -> {
                serverOfflinePlayers().forEach(offlinePlayers::add);

                Social.get().getLogger().info("Cached {} offline players!", offlinePlayers.size());
            });
        });
    }

    @Override
    public @NotNull Optional<UUID> resolve(@NotNull String username) {
        return this.offlinePlayers.stream()
            .filter(offlinePlayer -> offlinePlayer.username().equals(username))
            .map(OfflinePlayerReference::uuid)
            .findAny();
    }

    @Override
    public @NotNull Optional<OfflinePlayerReference> resolveOfflinePlayer(@NotNull UUID uuid) {
        return this.offlinePlayers.stream()
            .filter(offlinePlayer -> offlinePlayer.uuid().equals(uuid))
            .findAny();
    }

    protected boolean add(@NotNull OfflinePlayerReference offlinePlayer) {
        return this.offlinePlayers.add(offlinePlayer);
    }

    protected boolean remove(@NotNull UUID uuid) {
        return this.offlinePlayers.removeIf(offlinePlayer -> offlinePlayer.uuid().equals(uuid));
    }

    protected boolean remove(@NotNull OfflinePlayerReference offlinePlayer) {
        return this.remove(offlinePlayer.uuid());
    }

    protected boolean has(@NotNull UUID uuid) {
        return this.offlinePlayers.stream()
            .anyMatch(offlinePlayer -> offlinePlayer.uuid().equals(uuid));
    }

    protected boolean has(@NotNull OfflinePlayerReference offlinePlayer) {
        return has(offlinePlayer.uuid());
    }

    protected boolean update(@NotNull OfflinePlayerReference offlinePlayer) {
        if (has(offlinePlayer))
            remove(offlinePlayer);

        return add(offlinePlayer);
    }

}
