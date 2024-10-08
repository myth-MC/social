package ovh.mythmc.social.common.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.mythmc.social.api.Social;

public final class PluginUtil {

    @Setter
    @Getter
    private static JavaPlugin plugin;

    public static void runTask(Runnable runnable) {
        if (plugin == null) {
            Social.get().getLogger().error("A task was scheduled without SchedulerUtil being initialized!");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void registerEvents(Listener listener) {
        if (plugin == null) {
            Social.get().getLogger().error("An event was registered without SchedulerUtil being initialized!");
            return;
        }

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

}
