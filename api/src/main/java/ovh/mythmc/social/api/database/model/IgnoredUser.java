package ovh.mythmc.social.api.database.model;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.users.SocialUser;

@DatabaseTable(tableName = "ignored_users")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
public class IgnoredUser {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private @NotNull SocialUser user;

    @DatabaseField(canBeNull = false)
    private @NotNull UUID target;

    @DatabaseField(canBeNull = false)
    private @NotNull IgnoreScope scope;

    public enum IgnoreScope {
        ALL,
        CHAT,
        PRIVATE_MESSAGES
    }
    
}
