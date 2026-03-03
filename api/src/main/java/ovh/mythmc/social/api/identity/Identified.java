package ovh.mythmc.social.api.identity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a unique identity within the system.
 *
 * <p>
 * An {@code Identified} instance is defined by:
 * </p>
 * <ul>
 *     <li>A unique {@link UUID}</li>
 *     <li>A username</li>
 * </ul>
 */
public interface Identified {

    /**
     * Returns the unique identifier of this object.
     *
     * @return the {@link UUID} representing this identity
     */
    @NotNull
    UUID uuid();

    /**
     * Returns the username associated with this identity.
     *
     * @return the username
     */
    @NotNull
    String username();

}