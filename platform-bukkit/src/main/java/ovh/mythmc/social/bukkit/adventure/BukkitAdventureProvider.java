package ovh.mythmc.social.bukkit.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;

public class BukkitAdventureProvider extends SocialAdventureProvider {

    private final BukkitAudiences adventure;

    public BukkitAdventureProvider(final @NotNull JavaPlugin plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    @Override
    public Audience player(final @NotNull Player player) {
        return adventure.player(player);
    }

    @Override
    public Audience console() {
        return adventure.console();
    }

    @Override
    public Audience sender(final @NotNull CommandSender sender) {
        return adventure.sender(sender);
    }

}
