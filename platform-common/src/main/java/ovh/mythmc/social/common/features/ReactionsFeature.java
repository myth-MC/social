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
import ovh.mythmc.social.common.listeners.ReactionsListener;

@Feature(group = "social", identifier = "REACTIONS")
@FeatureConditionVersion(versions = { "1.21" } )
public final class ReactionsFeature {
    
    private final JavaPlugin plugin;

    private final ReactionsListener reactionsListener = new ReactionsListener();

    public ReactionsFeature(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getReactions().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(reactionsListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(reactionsListener);
        Social.get().getReactionManager().getReactionsMap().clear();
    }

}
