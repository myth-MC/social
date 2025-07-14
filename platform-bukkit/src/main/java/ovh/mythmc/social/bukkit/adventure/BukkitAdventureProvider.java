package ovh.mythmc.social.bukkit.adventure;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public class BukkitAdventureProvider extends SocialAdventureProvider {

    public static @NotNull BukkitAdventureProvider get() {
        return (BukkitAdventureProvider) SocialAdventureProvider.get();
    }

    private final BukkitAudiences adventure;

    public BukkitAdventureProvider(final @NotNull JavaPlugin plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    @Override
    public Audience user(@NotNull AbstractSocialUser user) {
        return adventure.player(Bukkit.getPlayer(user.uuid()));
    }

    @Override
    public Audience console() {
        return adventure.console();
    }

    public Audience player(final @NotNull Player player) {
        return adventure.player(player);
    }

}
