package ovh.mythmc.social.api.identity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface IdentityResolver {

    @NotNull
    Optional<Identified> fromUsername(final @NotNull String username);

    @NotNull
    Optional<Identified> fromUuid(final @NotNull UUID uuid);

    @NotNull
    Optional<UUID> resolveUuid(final @NotNull String username);

    @NotNull
    Optional<String> resolveUsername(final @NotNull UUID uuid);

}
