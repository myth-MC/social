package ovh.mythmc.social.api.user;

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

@DatabaseTable(tableName = "users")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
public final class DatabaseUser {

    public static DatabaseUser fromUser(@NotNull AbstractSocialUser<? extends Object> user) {
        return new DatabaseUser(user.uuid(), user.blockedChannels(), user.cachedName(), user.displayNameStyle(), user);
    }

    @DatabaseField(id = true)
    private final @NotNull UUID uuid;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private final ArrayList<String> blockedChannels;

    @DatabaseField(columnName = "cachedNickname")
    private final String cachedName;

    @DatabaseField(persisterClass = AdventureStylePersister.class)
    private final Style displayNameStyle;

    private final AbstractSocialUser<? extends Object> sourceUser;

    public <U extends AbstractSocialUser<? extends Object>> U toUser(U user) {
        user.setUuid(uuid);
        user.setBlockedChannels(blockedChannels);
        user.setCachedName(cachedName);
        user.setDisplayNameStyle(displayNameStyle);

        return user;
    }
    
}
