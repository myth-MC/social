package ovh.mythmc.social.api.identity;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.scheduler.SocialScheduler;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An abstract implementation of {@link IdentityResolver} used by platforms to
 * provide identities.
 */
public abstract class AbstractIdentityResolver implements IdentityResolver {

    private final Map<UUID, Identified> identitiesByUuid = new ConcurrentHashMap<>();

    private final Map<String, UUID> uuidByUsername = new ConcurrentHashMap<>();

    /**
     * Gets all identities present in the server.
     * @return an {@link Iterable} with every {@link Identified} identity present
     *         in the server
     */
    protected abstract Iterable<Identified> serverIdentities();

    protected AbstractIdentityResolver() {
        SocialScheduler.onPlatformInit(() -> {
            SocialScheduler.get().runAsyncTask(() -> {
                serverIdentities().forEach(this::add);
                Social.get().getLogger().info("Cached {} offline players!", identitiesByUuid.size());
            });
        });
    }

    @Override
    public @NotNull Optional<Identified> fromUsername(@NotNull String username) {
        String normalized = normalize(username);
        UUID uuid = uuidByUsername.get(normalized);
        if (uuid == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(identitiesByUuid.get(uuid));
    }

    @Override
    public @NotNull Optional<Identified> fromUuid(@NotNull UUID uuid) {
        return Optional.ofNullable(identitiesByUuid.get(uuid));
    }

    @Override
    public @NotNull Optional<UUID> resolveUuid(@NotNull String username) {
        return Optional.ofNullable(uuidByUsername.get(normalize(username)));
    }

    @Override
    public @NotNull Optional<String> resolveUsername(@NotNull UUID uuid) {
        return fromUuid(uuid).map(Identified::username);
    }

    protected void updateOrCreate(@NotNull Identified identity) {
        final UUID uuid = identity.uuid();
        final String username = identity.username();
        final String normalized = normalize(username);

        identitiesByUuid.compute(uuid, (key, oldValue) -> {

            // If user had a previous username, remove old index
            if (oldValue != null) {
                uuidByUsername.remove(normalize(oldValue.username()));
            }

            // Update username index
            uuidByUsername.put(normalized, uuid);

            return identity;
        });
    }

    protected void add(@NotNull Identified identity) {
        final UUID uuid = identity.uuid();
        final String normalized = normalize(identity.username());

        identitiesByUuid.put(uuid, identity);
        uuidByUsername.put(normalized, uuid);
    }

    private static String normalize(String username) {
        return username.toLowerCase(Locale.ROOT);
    }

}