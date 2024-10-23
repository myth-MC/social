package ovh.mythmc.social.common.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerServerLinks;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerLinks;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.List;

public final class ServerLinksListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialPlayer socialPlayer = Social.get().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (socialPlayer == null)
            return;

        List<WrapperCommonServerServerLinks.ServerLink> serverLinks = new ArrayList<>();
        Social.get().getConfig().getSettings().getPackets().getServerLinks().getLinks().forEach(serverLink -> {
            if (serverLink.type() != null) {
                WrapperCommonServerServerLinks.KnownType knownType = WrapperCommonServerServerLinks.KnownType.valueOf(serverLink.type());
                serverLinks.add(new WrapperCommonServerServerLinks.ServerLink(knownType, serverLink.url()));
            } else {
                Component customType = Social.get().getTextProcessor().parse(socialPlayer, socialPlayer.getMainChannel(), serverLink.displayName());
                serverLinks.add(new WrapperCommonServerServerLinks.ServerLink(customType, serverLink.url()));
            }
        });

        WrapperPlayServerServerLinks packet = new WrapperPlayServerServerLinks(serverLinks);
        PacketEvents.getAPI().getPlayerManager().sendPacket(event.getPlayer(), packet);
    }

}
