package ovh.mythmc.social.common.features;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listeners.IgnoreListener;

@Feature(group = "social", identifier = "IGNORE")
public final class IgnoreFeature {
    
    private final JavaPlugin plugin;

    private final IgnoreListener ignoreListener = new IgnoreListener();

    public IgnoreFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getCommands().getIgnore().enabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(ignoreListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(ignoreListener);
    }

}
