package ovh.mythmc.social.common.wrappers;

import java.net.URI;

import org.bukkit.Location;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

// This class acts as an utility for different platform implementations
public abstract class PlatformWrapper { 

    private static PlatformWrapper instance;

    public static @NotNull PlatformWrapper get() { return instance; }

    public static void set(@NotNull PlatformWrapper p) {
        instance = p;
    }

    public abstract void runGlobalTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable);

    public abstract void runRegionTask(@NotNull JavaPlugin plugin, @NotNull Location location, @NotNull Runnable runnable);

    public abstract void runAsyncTask(@NotNull JavaPlugin plugin, @NotNull Runnable runnable);

    public abstract void runEntityTask(@NotNull JavaPlugin plugin, @NotNull Entity entity, @NotNull Runnable runnable);

    public abstract void sendLink(@NotNull ServerLinks serverLinks, @NotNull Component displayName, @NotNull URI uri);

}
