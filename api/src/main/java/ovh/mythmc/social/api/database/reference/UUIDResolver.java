package ovh.mythmc.social.api.database.reference;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface UUIDResolver {

    @NotNull Optional<UUID> resolve(final @NotNull String username);

    @NotNull Optional<OfflinePlayerReference> resolveOfflinePlayer(final @NotNull UUID uuid);

}
