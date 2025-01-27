package ovh.mythmc.social.common.wrappers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

    /*
    public static void runGlobalTask(final @NotNull JavaPlugin plugin, final @NotNull Runnable runnable) {
        if (!isPaper()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }

        // paper
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void runRegionTask(final @NotNull JavaPlugin plugin, final @NotNull Runnable runnable) {
        if (!isPaper()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }

        // paper
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void runAsyncTask(final @NotNull JavaPlugin plugin, final @NotNull Runnable runnable) {
        if (!isPaper()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
            return;
        }

        // paper
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void runEntityTask(final @NotNull JavaPlugin plugin, final @NotNull Runnable runnable) {
        if (!isPaper()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }

        // paper
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static boolean isPaper() {
        boolean isPaper = false;
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) { }
        
        return isPaper;
    } */

}
