package ovh.mythmc.social.api.adventure;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class SocialAdventureProvider {

    private static SocialAdventureProvider socialAdventureProvider;

    public static void set(final @NotNull SocialAdventureProvider s) {
        socialAdventureProvider = s;
    }

    public static @NotNull SocialAdventureProvider get() { return socialAdventureProvider; }

    /*
    @Deprecated(forRemoval = true)
    public abstract void sendMessage(final @NotNull Player player,
                                     final @NotNull ComponentLike message,
                                     final @NotNull ChatChannel.Type type);

    @Deprecated(forRemoval = true)
    public void sendMessage(final @NotNull SocialUser user,
                            final @NotNull ComponentLike message,
                            final @NotNull ChatChannel.Type type) {
        sendMessage(user.getPlayer(), message, type);
    } */

    public abstract Audience player(final @NotNull Player player);

    public abstract Audience console();

    public abstract Audience sender(final @NotNull CommandSender sender);

}
