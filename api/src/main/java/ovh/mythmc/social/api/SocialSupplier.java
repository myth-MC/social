package ovh.mythmc.social.api;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Internal holder that stores the active {@link Social} instance.
 *
 * <p>
 * Platform implementations call {@link #set(Social)} once during startup.
 * All other callers should use {@link Social#get()} instead of this class
 * directly.
 */
public final class SocialSupplier {

    private static Social social;

    private SocialSupplier() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Registers the active {@link Social} instance.
     *
     * @param s the instance to register
     * @throws AlreadyInitializedException if the API has already been initialised
     * @throws NullPointerException        if {@code s} is {@code null}
     */
    public static void set(final @NotNull Social s) {
        if (social != null)
            throw new AlreadyInitializedException();

        social = Objects.requireNonNull(s);
    }

    /**
     * Returns the active {@link Social} instance.
     *
     * @return the social API instance
     */
    public static @NotNull Social get() {
        return social;
    }

}

