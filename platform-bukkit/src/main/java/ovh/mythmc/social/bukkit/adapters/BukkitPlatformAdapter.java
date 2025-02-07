package ovh.mythmc.social.bukkit.adapters;

import java.net.URI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ovh.mythmc.social.common.adapters.PlatformAdapter;

public final class BukkitPlatformAdapter extends PlatformAdapter {

    @Override
    public void runGlobalTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runRegionTask(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Runnable runnable) {
        runGlobalTask(plugin, runnable);
    }

    @Override
    public void runAsyncTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runEntityTask(@NotNull JavaPlugin plugin, @NotNull Entity entity, @NotNull Runnable runnable) {
        runGlobalTask(plugin, runnable);
    }

    @Override
    public void sendLink(@NotNull ServerLinks serverLinks, @NotNull Component displayName, @NotNull URI uri) {
        serverLinks.addLink(LegacyComponentSerializer.legacySection().serialize(displayName), uri);
    }

    @Override
    public void runAsyncTaskLater(@NotNull JavaPlugin plugin, @NotNull Runnable runnable, int ticks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
    }
    
}
