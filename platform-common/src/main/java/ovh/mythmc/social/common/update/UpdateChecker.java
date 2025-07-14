package ovh.mythmc.social.common.update;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
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

@UtilityClass
public class UpdateChecker {

    private final String url = "https://assets.mythmc.ovh/social/LATEST_VERSION";

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String latest = Social.get().version();

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    @Getter
    private boolean running = false;

    private final LoggerWrapper logger = new LoggerWrapper() {
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

    private void scheduleTask() {
        if (!Social.get().getConfig().getGeneral().isUpdateChecker())
            return;

        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, Social.get().getConfig().getGeneral().getUpdateCheckerIntervalInHours(), TimeUnit.HOURS);
    }

    private void performTask() {
        URLConnection connection = null;
        try {
            connection = URI.create(url).toURL().openConnection();
        } catch (IOException e) {
            if (Social.get().getConfig().getGeneral().isDebug())
                logger.warn(e.getMessage());
        }

        try (Scanner scanner = new Scanner(Objects.requireNonNull(connection).getInputStream())) {
            if (Social.get().getConfig().getGeneral().isDebug())
                logger.info("Checking for updates...");

            String latest = scanner.next();
            setLatest(latest);

            if (!Social.get().version().equals(latest)) {
                logger.info("A new update has been found: v" + latest + " (currently running v" + Social.get().version() + ")");
                logger.info("https://github.com/myth-MC/social/releases/" + latest);
                return;
            }

            if (Social.get().getConfig().getGeneral().isDebug())
                logger.info("No updates have been found.");
        } catch (IOException e) {
            if (Social.get().getConfig().getGeneral().isDebug())
                logger.warn(e.getMessage());
        }

        scheduleTask();
    }

    public void startTask() {
        if (running)
            return;

        running = true;
        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                performTask();
            }
        }, 1, TimeUnit.MINUTES);
    }
}