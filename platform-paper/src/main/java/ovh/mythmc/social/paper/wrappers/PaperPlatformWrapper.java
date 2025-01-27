package ovh.mythmc.social.paper.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.wrappers.PlatformWrapper;

public final class PaperPlatformWrapper extends PlatformWrapper {

    @Override
    public void runGlobalTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> {
            runnable.run();
        });
    }

    @Override
    public void runRegionTask(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Runnable runnable) {
        Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> {
            runnable.run();
        });
    }

    @Override
    public void runAsyncTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            runnable.run();
        });
    }

    @Override
    public void runEntityTask(@NotNull JavaPlugin plugin, @NotNull Entity entity, @NotNull Runnable runnable) {
        entity.getScheduler().run(plugin, scheduledTask -> {
            runnable.run();
        }, null);
    }
    
}
