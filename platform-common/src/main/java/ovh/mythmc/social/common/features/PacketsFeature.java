package ovh.mythmc.social.common.features;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureShutdown;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listeners.PacketsListener;

@Feature(group = "social", identifier = "PACKETS")
public final class PacketsFeature {

    private final JavaPlugin plugin;

    private PacketsListener packetsListener;

    public PacketsFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return isSupported() && Bukkit.getPluginManager().isPluginEnabled("PacketEvents") && Social.get().getConfig().getSettings().getPackets().isEnabled();
    }

    @FeatureEnable
    public void enable() {        
        this.packetsListener = new PacketsListener();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));

        // Load PacketEvents API
        PacketEvents.getAPI().load();

        // Initialize PacketEvents API
        PacketEvents.getAPI().init();

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
