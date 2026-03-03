package ovh.mythmc.social.api.user;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.chat.channel.SimpleChatChannel;
import ovh.mythmc.social.api.identity.IdentityResolver;

/**
 * Represents a class capable of holding all {@link SocialUser}s present
 * in the server.
 */
public interface SocialUserService {

    /**
     * Gets the {@link IdentityResolver} of this server
     * @return the {@link IdentityResolver} of this server
     */
    @NotNull
    IdentityResolver identityResolver();

    /**
     * Gets all current {@link SocialUser}s in this server
     * @return a {@link Set} with every {@link SocialUser} in the server
     */
    @NotNull
    Set<SocialUser> get();

    /**
     * Gets a specific {@link SocialUser} by its {@link UUID}.
     * 
     * <p>
     * If the {@link SocialUser} doesn't exist, it will be created by the
     * service.
     * </p>
     * @param uuid the {@link UUID} of the user
     * @return     a {@link SocialUser} matching the {@link UUID}
     */
    @NotNull
    SocialUser getOrCreate(@NotNull UUID uuid);

    /**
     * Gets a specific {@link SocialUser} by its {@link UUID}.
     * @param uuid the {@link UUID} of the user
     * @return     an {@link Optional} containing the {@link SocialUser} if
     *             present, or an empty {@link Optional} otherwise
     */
    @NotNull
    Optional<SocialUser> getByUuid(@NotNull UUID uuid);

    /**
     * Gets all {@link SocialUser}s with the spy feature on.
     * @return a {@link Collection} with all {@link SocialUser}s with the
     *         spy feature on
     */
    default Collection<SocialUser> getSocialSpyUsers() {
        return get().stream()
                .filter(user -> user.socialSpy().get())
                .toList();
    }

    /**
     * Gets all {@link SocialUser}s with the spy feature on in a specific
     * {@link ChatChannel}.
     * @param channel the {@link ChatChannel} to get the users from
     * @return        a {@link Collection} with every {@link SocialUser} with
     *                the spy feature on in the {@link ChatChannel}
     */
    default Collection<SocialUser> getSocialSpyUsersInChannel(@NotNull SimpleChatChannel channel) {
        return get().stream()
                .filter(user -> user.socialSpy().get() && user.mainChannel() != null
                        && user.mainChannel().equals(channel))
                .toList();
    }

}
