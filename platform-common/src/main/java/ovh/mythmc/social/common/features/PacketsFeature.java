package ovh.mythmc.social.common.features;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.annotations.status.FeatureShutdown;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listeners.PacketsListener;

@RequiredArgsConstructor
@Feature(key = "social", type = "PACKETS")
public final class PacketsFeature {

    private final JavaPlugin plugin;

    private final PacketsListener packetsListener = new PacketsListener();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return isSupported() && Social.get().getConfig().getSettings().getPackets().isEnabled();
    }

    @FeatureInitialize
    public void initialize() {
        if(!isSupported())
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));

        // Disable update checker
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();
        settings.checkForUpdates(false);

        // Load PacketEvents API
        PacketEvents.getAPI().load();

        // Initialize PacketEvents API
        PacketEvents.getAPI().init();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(packetsListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(packetsListener);
    }

    @FeatureShutdown
    public void shutdown() {
        PacketEvents.getAPI().terminate();
    }

    private boolean isSupported() {
        try {
            Class.forName("com.mohistmc.banner.bukkit.remapping.ReflectionHandler");
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }
    
}
