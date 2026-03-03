package ovh.mythmc.social.api.identity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves identity information between usernames and UUIDs.
 *
 * <p>
 * An {@code IdentityResolver} provides lookup mechanisms for retrieving
 * {@link Identified} instances or resolving individual identity properties
 * such as usernames and {@link UUID UUIDs}.
 * </p>
 *
 * @see Identified
 * @see AbstractIdentityResolver
 */
public interface IdentityResolver {

    /**
     * Attempts to retrieve an {@link Identified} instance from a username.
     *
     * @param username the username to resolve
     * @return an {@link Optional} containing the resolved identity,
     *         or empty if no match is found
     */
    @NotNull
    Optional<Identified> fromUsername(final @NotNull String username);

    /**
     * Attempts to retrieve an {@link Identified} instance from a UUID.
     *
     * @param uuid the UUID to resolve
     * @return an {@link Optional} containing the resolved identity,
     *         or empty if no match is found
     */
    @NotNull
    Optional<Identified> fromUuid(final @NotNull UUID uuid);

    /**
     * Resolves a UUID from a username.
     *
     * @param username the username to resolve
     * @return an {@link Optional} containing the corresponding UUID,
     *         or empty if no match is found
     */
    @NotNull
    Optional<UUID> resolveUuid(final @NotNull String username);

    /**
     * Resolves a username from a UUID.
     *
     * @param uuid the UUID to resolve
     * @return an {@link Optional} containing the corresponding username,
     *         or empty if no match is found
     */
    @NotNull
    Optional<String> resolveUsername(final @NotNull UUID uuid);

}
