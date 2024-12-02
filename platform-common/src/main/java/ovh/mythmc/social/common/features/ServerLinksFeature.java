package ovh.mythmc.social.common.features;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionVersion;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listeners.ServerLinksListener;

@Feature(group = "social", identifier = "SERVER_LINKS")
@FeatureConditionVersion(versions = {"1.21"})
public final class ServerLinksFeature {

    private final JavaPlugin plugin;

    private final ServerLinksListener serverLinksListener = new ServerLinksListener();

    public ServerLinksFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getPackets().isEnabled() &&
                Social.get().getConfig().getSettings().getPackets().getServerLinks().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(serverLinksListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(serverLinksListener);
    }

}
