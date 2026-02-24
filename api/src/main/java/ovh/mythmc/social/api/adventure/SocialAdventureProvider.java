package ovh.mythmc.social.api.adventure;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.user.SocialUser;

import org.jetbrains.annotations.NotNull;

/**
 * Provides Adventure {@link Audience} objects that route messages to social
 * users and the console.
 *
 * <p>
 * Platform adapters (e.g. Paper) register a concrete implementation on startup
 * via {@link #set(SocialAdventureProvider)}.
 */
public abstract class SocialAdventureProvider {

    private static SocialAdventureProvider socialAdventureProvider;

    /**
     * Registers the active adventure provider.
     *
     * @param s the provider to register
     */
    public static void set(final @NotNull SocialAdventureProvider s) {
        socialAdventureProvider = s;
    }

    /**
     * Returns the active adventure provider.
     *
     * @return the adventure provider
     */
    public static @NotNull SocialAdventureProvider get() {
        return socialAdventureProvider;
    }

    /**
     * Returns the audience that wraps the given user.
     *
     * @param user the social user
     * @return the corresponding audience
     */
    public abstract Audience user(final @NotNull SocialUser user);

    /**
     * Returns the audience that wraps the server console.
     *
     * @return the console audience
     */
    public abstract Audience console();

}
