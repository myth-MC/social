package ovh.mythmc.social.common.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emojis.Emoji;

import java.util.EnumSet;
import java.util.UUID;

public final class PacketsListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Social.get().getConfig().getSettings().getEmojis().isEnabled())
            return;

        for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
            if (emoji.name().length() > 14)
                continue;

            UUID uuid = UUID.randomUUID();

            WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(
                    EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER),
                    new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                            new UserProfile(uuid, ":" + emoji.name() + ":"),
                            false,
                            100,
                            GameMode.CREATIVE,
                            null,
                            null
                    )
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(event.getPlayer(), packet);
        }
    }

}
