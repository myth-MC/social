package ovh.mythmc.social.api.database.model;

import java.util.ArrayList;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.kyori.adventure.text.format.Style;
import ovh.mythmc.social.api.database.persister.AdventureMutableStylePersister;
import ovh.mythmc.social.api.database.persister.MutableStringPersister;
import ovh.mythmc.social.api.util.Mutable;

@DatabaseTable(tableName = "users")
public abstract class DatabaseUser {

    @DatabaseField(id = true)
    protected UUID uuid;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    protected ArrayList<String> blockedChannels = new ArrayList<>();

    @DatabaseField(columnName = "cachedMainChannel", dataType = DataType.SERIALIZABLE)
    protected Mutable<String> cachedMainChannelName = Mutable.empty();

    @DatabaseField(columnName = "cachedNickname", persisterClass = MutableStringPersister.class)
    protected Mutable<String> cachedDisplayName = Mutable.empty();

    @DatabaseField(persisterClass = AdventureMutableStylePersister.class)
    protected Mutable<Style> displayNameStyle = Mutable.of(Style.empty());

    public abstract boolean clearFromCache();

    protected DatabaseUser() {
    }

    protected DatabaseUser(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID uuid() {
        return this.uuid;
    }

    public @NotNull ArrayList<String> blockedChannels() {
        return this.blockedChannels;
    }

    public @NotNull Mutable<String> cachedMainChannelName() {
        if (this.cachedMainChannelName == null)
            this.cachedMainChannelName = Mutable.empty();

        return this.cachedMainChannelName;
    }

    public @NotNull Mutable<String> cachedDisplayName() {
        if (this.cachedDisplayName == null)
            this.cachedDisplayName = Mutable.empty();

        return this.cachedDisplayName;
    }

    public @NotNull Mutable<Style> displayNameStyle() {
        if (this.displayNameStyle == null)
            this.displayNameStyle = Mutable.of(Style.empty());

        return this.displayNameStyle;
    }
    
}
