package ovh.mythmc.social.api.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.database.persister.AdventureStylePersister;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.user.SocialUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialDatabase {

    private static final SocialDatabase instance = new SocialDatabase();

    public static SocialDatabase get() {
        return instance;
    }

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

    private Dao<SocialUser, UUID> usersDao;

    private Map<UUID, SocialUser> usersCache = new HashMap<>();

    private boolean firstBoot = false;

    public void initialize(@NotNull String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);

        // Custom persisters
        final var adventureStylePersister = AdventureStylePersister.getSingleton();
        DataPersisterManager.registerDataPersisters(adventureStylePersister);

        // Users table
        TableUtils.createTableIfNotExists(connectionSource, SocialUser.class);

        // Define DAOs
        usersDao = DaoManager.createDao(connectionSource, SocialUser.class);

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

    public void create(final @NotNull SocialUser user) {
        try {
            usersDao.createIfNotExists(user);
        } catch (SQLException e) {
            logger.error("Exception while creating user {}", e);
        }
    }

    public void delete(final @NotNull SocialUser user) {
        try {
            usersDao.delete(user);
        } catch (SQLException e) {
            logger.error("Exception while deleting user {}", e);
        }
    }

    public void update(final @NotNull SocialUser user) {
        try {
            if (usersCache.containsKey(user.getUuid())) {
                usersCache.put(user.getUuid(), user);
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

    private void updateEntry(final @NotNull SocialUser user) {
        try {
            usersDao.update(user);

            // Clear cache value
            if (user.player().isEmpty())
                usersCache.remove(user.getUuid());
        } catch (SQLException e) {
            logger.error("Exception while updating entry {}", e);
        }
    }

    public Collection<SocialUser> getUsers() {
        try {
            return usersDao.queryForAll();
        } catch (SQLException e) {
            logger.error("Exception while getting all users {}" , e);
        }

        return null;
    }

    public SocialUser getUserByUuid(final @NotNull UUID uuid) {
        if (usersCache.containsKey(uuid))
            return usersCache.get(uuid);

        try {
            SocialUser user = usersDao.queryForId(uuid);
            if (user == null)
                return null;

            usersCache.put(uuid, user);
            return user;
        } catch (SQLException e) {
            logger.error("Exception while getting account {}", e);
        }

        return null;
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
