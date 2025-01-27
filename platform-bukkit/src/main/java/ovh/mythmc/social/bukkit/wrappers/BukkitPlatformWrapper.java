package ovh.mythmc.social.bukkit.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.wrappers.PlatformWrapper;

public final class BukkitPlatformWrapper extends PlatformWrapper {

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
    
}
