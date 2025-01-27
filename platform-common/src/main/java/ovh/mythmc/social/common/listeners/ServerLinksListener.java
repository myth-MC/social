package ovh.mythmc.social.common.listeners;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.wrappers.PlatformWrapper;

import java.net.URI;

public final class ServerLinksListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ServerLinks serverLinks = Bukkit.getServerLinks().copy();

        SocialUser user = Social.get().getUserManager().get(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        Social.get().getConfig().getServerLinks().getLinks().forEach(serverLink -> {
            URI uri = URI.create(serverLink.url());

            if (serverLink.type() == null) {
                Component displayName = Social.get().getTextProcessor().parse(user, user.getMainChannel(), Component.text(serverLink.displayName()));

                PlatformWrapper.get().sendLink(serverLinks, displayName, uri);
                return;
            }

            ServerLinks.Type type = ServerLinks.Type.valueOf(serverLink.type());
            serverLinks.addLink(type, uri);

            event.getPlayer().sendLinks(serverLinks);
        });
    }

}
