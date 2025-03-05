package ovh.mythmc.social.api.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public abstract class SocialScheduler {

    protected static SocialScheduler instance;

    public static @NotNull SocialScheduler get() { return instance; }

    public static void set(@NotNull SocialScheduler s) {
        instance = s;
    }

    public abstract void runGlobalTask(@NotNull Runnable runnable);

    public abstract void runAsyncTaskLater(@NotNull Runnable runnable, int ticks);

    public abstract void runAsyncTask(@NotNull Runnable runnable);
    
}
