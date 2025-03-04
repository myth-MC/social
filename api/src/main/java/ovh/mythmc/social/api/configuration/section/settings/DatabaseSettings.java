package ovh.mythmc.social.api.configuration.section.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class DatabaseSettings {

    @Comment("Database type (SQLITE, MYSQL or MARIADB)")
    private DatabaseType type = DatabaseType.SQLITE;

    @Comment("Time between each cache clean in minutes")
    private int cacheClearInterval = 5;

    @Comment("Don't change this, you might lose all your data")
    private boolean initialized = false;

    @Comment("Don't change this, you might lose all your data")
    private int databaseVersion = 0;

    public void setVersion(int version) {
        databaseVersion = version;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public enum DatabaseType {
        SQLITE,
        MYSQL,
        MARIADB
    }
    
}
