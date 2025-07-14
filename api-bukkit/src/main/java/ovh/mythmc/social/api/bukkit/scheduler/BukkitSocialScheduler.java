package ovh.mythmc.social.api.bukkit.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.scheduler.SocialScheduler;

public abstract class BukkitSocialScheduler extends SocialScheduler {

    public static @NotNull BukkitSocialScheduler get() {
        if (instance instanceof BukkitSocialScheduler bukkitSocialScheduler)
            return bukkitSocialScheduler;

        return null;
    }

    public abstract void runRegionTask(@NotNull Location location, @NotNull Runnable runnable);

    public abstract void runEntityTask(@NotNull Entity entity, @NotNull Runnable runnable);
    
}
