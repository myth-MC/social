package ovh.mythmc.social.api.players;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ovh.mythmc.social.api.chat.ChatChannel;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class SocialPlayer {

    private final UUID uuid;

    private ChatChannel mainChannel;

    private boolean muted;

    private boolean socialSpy;

    private long latestMessageInMilliseconds = 0L;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getNickname() {
        return ChatColor.stripColor(getPlayer().getDisplayName());
    }

}
