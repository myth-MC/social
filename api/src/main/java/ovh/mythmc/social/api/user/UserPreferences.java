package ovh.mythmc.social.api.user;

import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.util.Mutable;

/**
 * Represents the preferences of a {@link SocialUser}.
 */
public interface UserPreferences {

    /**
     * Gets the {@link Mutable} display name of this user.
     * @return a {@link Mutable} containing the display name of this user
     *         in the form of a {@link TextComponent}
     */
    @NotNull
    Mutable<TextComponent> displayName();

    /**
     * Gets the status of the spy feature for this user.
     * @return a {@link Mutable} containing a {@link Boolean}
     */
    @NotNull
    Mutable<Boolean> socialSpy();

    /**
     * Gets the timestamp of the last message sent by this user.
     * @return a {@link Mutable} containing the timestamp of the last message
     *         sent by this user in the form of a {@link Long}
     */
    @NotNull
    Mutable<Long> lastMessageTimestamp();

}
