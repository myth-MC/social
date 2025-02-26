package ovh.mythmc.social.paper.adapter;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.common.adapter.PlatformAdapter;

public final class PaperPlatformAdapter extends PlatformAdapter {

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

    @Override
    public void sendLink(@NotNull ServerLinks serverLinks, @NotNull Component displayName, @NotNull URI uri) {
        serverLinks.addLink(displayName, uri);
    }

    @Override
    public void runAsyncTaskLater(@NotNull JavaPlugin plugin, @NotNull Runnable runnable, int ticks) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> {
            runnable.run();
        }, ticks * 50, TimeUnit.MILLISECONDS );
    }
    
}
