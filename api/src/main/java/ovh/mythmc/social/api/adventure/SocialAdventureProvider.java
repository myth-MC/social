package ovh.mythmc.social.api.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.players.SocialPlayer;

public abstract class SocialAdventureProvider {

    private static SocialAdventureProvider socialAdventureProvider;

    public static void set(final @NotNull SocialAdventureProvider s) {
        socialAdventureProvider = s;
    }

    public static @NotNull SocialAdventureProvider get() { return socialAdventureProvider; }

    public abstract void sendMessage(final @NotNull Player player,
                                     final @NotNull ComponentLike message,
                                     final @NotNull ChannelType type);

    public void sendMessage(final @NotNull SocialPlayer socialPlayer,
                            final @NotNull ComponentLike message,
                            final @NotNull ChannelType type) {
        sendMessage(socialPlayer.getPlayer(), message, type);
    }

    public abstract Audience player(final @NotNull Player player);

    public abstract Audience console();

    public abstract Audience sender(final @NotNull CommandSender sender);

}
