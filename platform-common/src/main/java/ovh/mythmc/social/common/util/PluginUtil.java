package ovh.mythmc.social.common.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class PluginUtil { 

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
    }

}
