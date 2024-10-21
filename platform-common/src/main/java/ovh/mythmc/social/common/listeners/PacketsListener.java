package ovh.mythmc.social.common.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.text.keywords.SocialKeyword;
import ovh.mythmc.social.api.text.parsers.SocialParser;

import java.util.EnumSet;
import java.util.UUID;

public final class PacketsListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Emojis completion
        if (Social.get().getConfig().getSettings().getEmojis().isEnabled() &&
                Social.get().getConfig().getSettings().getPackets().isChatEmojiTabCompletion()) {

            for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
                createFakePlayer(event.getPlayer(), ":" + emoji.name() + ":");
            }
        }

        // Keyword completion
        if (Social.get().getConfig().getSettings().getPackets().isChatKeywordTabCompletion()) {
            for (SocialParser parser  : Social.get().getTextProcessor().getParsers()) {
                if (parser instanceof SocialKeyword keyword) {
                    createFakePlayer(event.getPlayer(), "[" + keyword.keyword() + "]");
                }
            }
        }
    }

    private void createFakePlayer(Player player, String name) {
        if (name.length() > 14)
            return;

        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(
                EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER),
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(UUID.randomUUID(), name),
                        false,
                        100,
                        GameMode.CREATIVE,
                        null,
                        null
                )
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

}
