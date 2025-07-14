package ovh.mythmc.social.api.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.HashSet;
import java.util.Set;

@Internal
public abstract class SocialScheduler {

    protected static final Set<Runnable> platformInitTasks = new HashSet<>();

    protected static SocialScheduler instance;

    public static @NotNull SocialScheduler get() { return instance; }

    public static void set(@NotNull SocialScheduler s) {
        instance = s;
    }

    public static void onPlatformInit(@NotNull Runnable runnable) {
        if (instance == null) {
            platformInitTasks.add(runnable);
        } else { // Fallback
            get().runGlobalTask(runnable);
        }
    }

    protected SocialScheduler() {
        platformInitTasks.forEach(this::runGlobalTask);
        platformInitTasks.clear();
    }

    public abstract void runGlobalTask(@NotNull Runnable runnable);

    public abstract void runAsyncTaskLater(@NotNull Runnable runnable, int ticks);

    public abstract void runAsyncTask(@NotNull Runnable runnable);
    
}
