package ovh.mythmc.social.paper.scheduler;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.bukkit.scheduler.BukkitSocialSchedulerImpl;

public class PaperSocialSchedulerImpl extends BukkitSocialSchedulerImpl {

    @Override
    public void runRegionTask(@NotNull Location location, @NotNull Runnable runnable) {
        Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> {
            runnable.run();
        });
    }

    @Override
    public void runEntityTask(@NotNull Entity entity, @NotNull Runnable runnable) {
        entity.getScheduler().run(plugin, scheduledTask -> {
            runnable.run();
        }, null);
    }

    @Override
    public void runGlobalTask(@NotNull Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> {
            runnable.run();
        });
    }

    @Override
    public void runAsyncTaskLater(@NotNull Runnable runnable, int ticks) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> {
            runnable.run();
        }, ticks * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAsyncTask(@NotNull Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
            runnable.run();
        });
    }

}
