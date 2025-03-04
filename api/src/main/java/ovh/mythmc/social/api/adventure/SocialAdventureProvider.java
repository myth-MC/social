package ovh.mythmc.social.api.adventure;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public abstract class SocialAdventureProvider {

    private static SocialAdventureProvider socialAdventureProvider;

    public static void set(final @NotNull SocialAdventureProvider s) {
        socialAdventureProvider = s;
    }

    public static @NotNull SocialAdventureProvider get() { return socialAdventureProvider; }

    public abstract Audience console();

}
