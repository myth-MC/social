package ovh.mythmc.social.common.features;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.PacketsListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class PacketsFeature implements SocialFeature {

    private final PacketsListener packetsListener = new PacketsListener();

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.OTHER;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getPackets().isEnabled();
    }

    @Override
    public void initialize() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(PluginUtil.getPlugin()));

        // Disable update checker
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();
        settings.checkForUpdates(false);

        // Load PacketEvents API
        PacketEvents.getAPI().load();

        // Initialize PacketEvents API
        PacketEvents.getAPI().init();
    }

    @Override
    public void enable() {
        // Register listener
        PluginUtil.registerEvents(packetsListener);
    }

    @Override
    public void disable() {
        // Unregister listener
        HandlerList.unregisterAll(packetsListener);
    }

    @Override
    public void shutdown() {
        // Terminate PacketEvents API
        PacketEvents.getAPI().terminate();
    }

}
