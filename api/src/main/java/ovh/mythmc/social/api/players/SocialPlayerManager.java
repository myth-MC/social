package ovh.mythmc.social.api.players;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialPlayerManager {

    public static final SocialPlayerManager instance = new SocialPlayerManager();
    private static final List<SocialPlayer> playerList = new ArrayList<>();

    public @NotNull List<SocialPlayer> get() {
        return List.copyOf(playerList);
    }

    public SocialPlayer get(final @NotNull UUID uuid) {
        for (SocialPlayer player : playerList) {
            if (player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    public void registerSocialPlayer(final @NotNull SocialPlayer socialPlayer) {
        playerList.add(socialPlayer);
    }

    public void unregisterSocialPlayer(final @NotNull SocialPlayer socialPlayer) {
        playerList.remove(socialPlayer);
    }

    @ApiStatus.Internal
    public void clear() { playerList.clear(); }

}
