package ovh.mythmc.social.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.bukkit.scheduler.BukkitSocialScheduler;

public class BukkitSocialSchedulerImpl extends BukkitSocialScheduler {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    @Override
    public void runRegionTask(@NotNull Location location, @NotNull Runnable runnable) {
        runGlobalTask(runnable);
    }

    @Override
    public void runEntityTask(@NotNull Entity entity, @NotNull Runnable runnable) {
        runGlobalTask(runnable);
    }

    @Override
    public void runGlobalTask(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runAsyncTaskLater(@NotNull Runnable runnable, int ticks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
    }

    @Override
    public void runAsyncTask(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    
}
