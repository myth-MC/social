package ovh.mythmc.social.api.database.model;

import java.util.ArrayList;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.database.persister.AdventureStylePersister;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@Getter
@DatabaseTable(tableName = "users")
public abstract class DatabaseUser {

    @DatabaseField(id = true)
    protected @NotNull UUID uuid;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    protected ArrayList<String> blockedChannels = new ArrayList<>();

    @DatabaseField(columnName = "cachedNickname")
    protected String cachedDisplayName;

    @DatabaseField(persisterClass = AdventureStylePersister.class)
    protected Style displayNameStyle;

    public abstract boolean clearFromCache();

    protected DatabaseUser() {
    }
    
}
