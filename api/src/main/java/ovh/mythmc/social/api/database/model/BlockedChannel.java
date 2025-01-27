package ovh.mythmc.social.api.database.model;

import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.users.SocialUser;

@DatabaseTable(tableName = "blocked_channels")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
public class BlockedChannel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
    private @NotNull SocialUser user;

    @DatabaseField(canBeNull = false)
    private @NotNull String channelName;
    
}
