package ovh.mythmc.social.common.update;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.logger.LoggerWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for checking for updates.
 */
public final class UpdateChecker {

    private static final String URL = "https://assets.mythmc.ovh/social/LATEST_VERSION";

    private static String latest = Social.get().version();

    private static final ScheduledExecutorService ASYNC_SCHEDULER = Executors.newScheduledThreadPool(1);

    private static boolean running = false;

    private static final LoggerWrapper LOGGER = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Social.get().getLogger().info("[update-checker] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Social.get().getLogger().warn("[update-checker] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Social.get().getLogger().error("[update-checker] " + message, args);
        }
    };

    private UpdateChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull String getLatest() {
        return latest;
    }

    private static void setLatest(@NotNull String latest) {
        UpdateChecker.latest = latest;
    }

    public static boolean isRunning() {
        return running;
    }

    private static void scheduleTask() {
        if (!Social.get().getConfig().getGeneral().isUpdateChecker())
            return;

        ASYNC_SCHEDULER.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, Social.get().getConfig().getGeneral().getUpdateCheckerIntervalInHours(), TimeUnit.HOURS);
    }

    private static void performTask() {
        URLConnection connection = null;
        try {
            connection = URI.create(URL).toURL().openConnection();
        } catch (IOException e) {
            if (Social.get().getConfig().getGeneral().isDebug())
                LOGGER.warn(e.getMessage());
        }

        try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
            if (Social.get().getConfig().getGeneral().isDebug())
                LOGGER.info("Checking for updates...");

            String latest = scanner.next();
            setLatest(latest);

            if (!Social.get().version().equals(latest)) {
                LOGGER.info("A new update has been found: v" + latest + " (currently running v" + Social.get().version() + ")");
                LOGGER.info("https://github.com/myth-MC/social/releases/" + latest);
                return;
            }

            if (Social.get().getConfig().getGeneral().isDebug())
                LOGGER.info("No updates have been found.");
        } catch (IOException e) {
            if (Social.get().getConfig().getGeneral().isDebug())
                LOGGER.warn(e.getMessage());
        }

        scheduleTask();
    }

    public static void startTask() {
        if (running)
            return;

        running = true;
        ASYNC_SCHEDULER.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, 1, TimeUnit.MINUTES);
    }
}