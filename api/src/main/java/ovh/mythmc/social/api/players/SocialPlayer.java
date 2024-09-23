package ovh.mythmc.social.api.players;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.chat.ChatChannel;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class SocialPlayer {

    private final UUID uuid;

    private String nickname;

    private ChatChannel mainChannel;

    private boolean muted;

    private boolean socialSpy;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}
