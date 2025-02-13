package ovh.mythmc.social.common.features;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.social.api.users.SocialUserCompanion;
import ovh.mythmc.social.common.listeners.CompanionListener;

@Feature(group = "social", identifier = "MESSAGING")
public final class CompanionFeature {

    private final JavaPlugin plugin;

    private final CompanionListener listener;

    public CompanionFeature(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.listener = new CompanionListener(plugin);
    }

    @FeatureInitialize
    public void initialize() {
        SocialUserCompanion.S2C_CHANNELS.forEach(channelName -> {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelName);
        });

        SocialUserCompanion.C2S_CHANNELS.forEach(channelName -> {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channelName, listener);
        });
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        listener.registerCallbackHandlers();
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(listener);

        listener.unregisterCallbackHandlers();
    }
    
}
