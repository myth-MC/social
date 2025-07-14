package ovh.mythmc.social.api.database.reference;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class OfflinePlayerReference {

    public static OfflinePlayerReference of(@NotNull UUID uuid, @NotNull String username) {
        return new OfflinePlayerReference(uuid, username);
    }

    private final UUID uuid;

    private final String username;

    private OfflinePlayerReference(@NotNull UUID uuid, @NotNull String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public @NotNull UUID uuid() {
        return this.uuid;
    }

    public @NotNull String username() {
        return this.username;
    }

}
