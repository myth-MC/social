package ovh.mythmc.social.api.presence;

/**
 * Represents the online presence state of an entity.
 *
 * <p>
 * {@code OnlinePresence} defines whether an entity is currently online
 * and whether its cached presence information may be cleared.
 */
public interface OnlinePresence {

    /**
     * Determines whether the entity is currently online.
     *
     * @return {@code true} if the entity is online,
     *         {@code false} otherwise
     */
    boolean isOnline();

    /**
     * Determines whether this entity should be removed from cache.
     *
     *
     * @return {@code true} if the entity can be cleared from cache,
     *         {@code false} otherwise
     */
    boolean isExpired();

}