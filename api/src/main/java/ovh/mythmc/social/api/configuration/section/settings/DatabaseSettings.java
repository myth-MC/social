package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;

/**
 * Settings for the database module.
 */
@Configuration
public class DatabaseSettings {

    @Comment("Database type (SQLITE, MYSQL or MARIADB)")
    private DatabaseType type = DatabaseType.SQLITE;

    @Comment("Time between each cache clean in minutes")
    private int cacheClearInterval = 5;

    @Comment("Don't change this, you might lose all your data")
    private boolean initialized = false;

    @Comment("Don't change this, you might lose all your data")
    private int databaseVersion = 0;

    public @NotNull DatabaseType getType() {
        return type;
    }

    public int getCacheClearInterval() {
        return cacheClearInterval;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setVersion(int version) {
        databaseVersion = version;
    }

    public enum DatabaseType {
        SQLITE,
        MYSQL,
        MARIADB
    }
    
}

