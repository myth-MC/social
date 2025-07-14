package ovh.mythmc.social.sponge.scheduler;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import ovh.mythmc.social.api.scheduler.SocialScheduler;

public final class SpongeSocialSchedulerImpl extends SocialScheduler {

    @Override
    public void runGlobalTask(@NotNull Runnable runnable) {
        final Task task = Task.builder()
            .execute(runnable)
            .build();

        Sponge.server().scheduler().submit(task);
    }

    @Override
    public void runAsyncTaskLater(@NotNull Runnable runnable, int ticks) {
        final Task task = Task.builder()
            .execute(runnable)
            .delay(Ticks.of(ticks))
            .build();

        Sponge.asyncScheduler().submit(task);
    }

    @Override
    public void runAsyncTask(@NotNull Runnable runnable) {
        final Task task = Task.builder()
            .execute(runnable)
            .build();

        Sponge.asyncScheduler().submit(task);
    }

}
