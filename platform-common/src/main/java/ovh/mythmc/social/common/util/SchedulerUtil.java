package ovh.mythmc.social.common.util;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ovh.mythmc.social.api.Social;

public final class SchedulerUtil {

    @Setter
    private static JavaPlugin plugin;

    public static void runTask(Runnable runnable) {
        if (plugin == null) {
            Social.get().getLogger().error("A task was scheduled without SchedulerUtil being initialized!");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, runnable);
    }

}
