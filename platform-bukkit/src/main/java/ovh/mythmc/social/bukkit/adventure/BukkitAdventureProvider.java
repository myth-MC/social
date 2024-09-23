package ovh.mythmc.social.bukkit.adventure;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;

public class BukkitAdventureProvider extends SocialAdventureProvider {

    private final BukkitAudiences adventure;

    public BukkitAdventureProvider(final @NotNull JavaPlugin plugin) {
        this.adventure = BukkitAudiences.create(plugin);

        SocialAdventureProvider.set(this);
    }

    @Override
    public void sendMessage(final @NotNull Player player, final @NotNull ComponentLike message) {
        if (adventure == null)
            return;

        adventure.player(player).sendMessage(message);
    }

}
