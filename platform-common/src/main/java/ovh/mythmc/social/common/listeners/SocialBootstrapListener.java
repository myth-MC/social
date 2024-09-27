package ovh.mythmc.social.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.common.text.placeholders.PAPIExpansion;

public final class SocialBootstrapListener implements Listener {

    @EventHandler
    public void onSocialBootstrap(SocialBootstrapEvent event) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new PAPIExpansion();
    }

}
