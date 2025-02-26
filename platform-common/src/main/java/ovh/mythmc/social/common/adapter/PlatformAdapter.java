package ovh.mythmc.social.common.adapter;

import java.net.URI;

import org.bukkit.Location;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

// This class adapts methods for different platform implementations
public abstract class PlatformAdapter { 

    private static PlatformAdapter instance;

    public static @NotNull PlatformAdapter get() { return instance; }

    public static void set(@NotNull PlatformAdapter p) {
        instance = p;
    }

    public abstract void runGlobalTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable);

    public abstract void runAsyncTaskLater(@NotNull JavaPlugin plugin, @NotNull Runnable runnable, int ticks);

    public abstract void runRegionTask(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Runnable runnable);

    public abstract void runAsyncTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable);

    public abstract void runEntityTask(@NotNull JavaPlugin plugin, @NotNull Entity entity, @NotNull Runnable runnable);

    public abstract void sendLink(@NotNull ServerLinks serverLinks, @NotNull Component displayName, @NotNull URI uri);

}
