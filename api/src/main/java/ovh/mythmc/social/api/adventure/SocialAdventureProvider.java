package ovh.mythmc.social.api.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.Objects;
import java.util.UUID;

public abstract class SocialAdventureProvider {

    private static SocialAdventureProvider socialAdventureProvider;

    public static void set(final @NotNull SocialAdventureProvider s) {
        socialAdventureProvider = s;
    }

    public static @NotNull SocialAdventureProvider get() { return socialAdventureProvider; }

    public abstract void sendMessage(final @NotNull Player player,
                                     final @NotNull ComponentLike message);

    public void sendMessage(final @NotNull SocialPlayer socialPlayer,
                            final @NotNull ComponentLike message) {
        sendMessage(socialPlayer.getPlayer(), message);
    }

    public void sendMessage(final @NotNull UUID uuid,
                            final @NotNull ComponentLike message) {
        sendMessage(Objects.requireNonNull(Social.get().getPlayerManager().get(uuid)), message);
    }

    public abstract Audience getSender(final @NotNull CommandSender sender);

}
