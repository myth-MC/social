package ovh.mythmc.social.api.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.database.persister.AdventureStylePersister;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.user.SocialUser;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Internal
public final class SocialDatabase<T extends AbstractSocialUser> {

    private static SocialDatabase<? extends AbstractSocialUser> instance;

    public static <T extends AbstractSocialUser> void newInstance(@NotNull Class<T> type) {
        instance = new SocialDatabase<>(type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSocialUser> SocialDatabase<T> get() {
        return (SocialDatabase<T>) instance;
    }

    private final Class<T> type;

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Social.get().getLogger().info("[database] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Social.get().getLogger().warn("[database] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Social.get().getLogger().error("[database] " + message, args);
        }
    };

    private Dao<T, UUID> usersDao;

    private final Map<UUID, T> usersCache = new HashMap<>();

    private boolean firstBoot = false;

    public void initialize(@NotNull String path) throws SQLException {
        Logger.setGlobalLogLevel(Level.ERROR); // Disable unnecessary verbose

        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);

        // Custom persisters
        final var adventureStylePersister = AdventureStylePersister.getSingleton();
        DataPersisterManager.registerDataPersisters(adventureStylePersister);

        // Users table
        TableUtils.createTableIfNotExists(connectionSource, type);

        // Define DAOs
        usersDao = DaoManager.createDao(connectionSource, type);

        // Upgrade database
        firstBoot = !Social.get().getConfig().getDatabaseSettings().isInitialized() &&
            Social.get().getConfig().getDatabaseSettings().getDatabaseVersion() == 0;

        upgrade();

        // Schedule auto-saver
        scheduleAutoSaver();

        // Mark database as initialized
        Social.get().getConfig().setDatabaseInitialized();
    }

    public void shutdown() {
        // Update all entries before shutting down
        updateAllEntries();
    }

    public void create(final @NotNull T user) {
        try {
            usersDao.createIfNotExists(user);
        } catch (SQLException e) {
            logger.error("Exception while creating user {}", e);
        }
    }

    public void delete(final @NotNull T user) {
        try {
            usersDao.delete(user);
        } catch (SQLException e) {
            logger.error("Exception while deleting user {}", e);
        }
    }

    public void update(final @NotNull T user) {
        try {
            if (usersCache.containsKey(user.uuid())) {
                usersCache.put(user.uuid(), user);
                return;
            }

            usersDao.update(user);
        } catch (SQLException e) {
            logger.error("Exception while updating user {}", e);
        }
    }

    private void scheduleAutoSaver() {
        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                // Update all entries
                updateAllEntries();

                // Schedule next task
                scheduleAutoSaver();
            }
        }, 5, TimeUnit.MINUTES);
    }

    private void updateAllEntries() {
        Map.copyOf(usersCache).values().forEach(this::updateEntry);
    }

    private void updateEntry(final @NotNull T user) {
        try {
            usersDao.update(user);

            // Clear cache value
            if (user.clearFromCache())
                usersCache.remove(user.uuid());
        } catch (SQLException e) {
            logger.error("Exception while updating entry {}", e);
        }
    }

    public Collection<T> getUsers() {
        try {
            return usersDao.queryForAll();
        } catch (SQLException e) {
            logger.error("Exception while getting all users {}" , e);
        }

        return null;
    }

    public T getUserByUuid(final @NotNull UUID uuid) {
        if (usersCache.containsKey(uuid))
            return usersCache.get(uuid);

        try {
            T user = usersDao.queryForId(uuid);
            if (user == null)
                return null;

            usersCache.put(uuid, user);
            return user;
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }

    public T getUserByName(final @NotNull String name) {
        T cachedUser = findCachedUserByName(name);
        if (cachedUser != null)
            return cachedUser;

        try {
            List<T> users = usersDao.queryBuilder()
                .where()
                .eq("cachedNickname", name)
                .query();

            if (users != null && !users.isEmpty()) {
                T user = users.getFirst();

                if (user.isOnline()) // We'll only cache the result if the player is online
                    usersCache.put(user.uuid(), user);

                return user;
            }
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
    }

    private T findCachedUserByName(final @NotNull String name) {
        return usersCache.values().stream()
            .filter(SocialUser::isOnline)
            .filter(user -> user.name().equals(name))
            .findFirst().orElse(null);
    }

    private void upgrade() {
        if (!firstBoot) {
            final int currentVersion = Social.get().getConfig().getDatabaseSettings().getDatabaseVersion();
            if (currentVersion < 1) {
                try {
                    logger.info("Upgrading database...");
                    usersDao.executeRaw("ALTER TABLE `users` ADD COLUMN displayNameStyle STRING;");
                    logger.info("Done!");
                } catch (SQLException e) {
                    logger.error("Exception while upgrading database: {}", e);
                }
            }
        }

        Social.get().getConfig().updateDatabaseVersion(1);
    }
    
}
