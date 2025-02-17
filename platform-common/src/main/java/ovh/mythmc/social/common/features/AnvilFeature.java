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
import ovh.mythmc.social.common.listeners.AnvilListener;

@Feature(group = "social", identifier = "EMOJIS")
@FeatureConditionVersion(versions = "1.21")
public final class AnvilFeature {

    private final JavaPlugin plugin;

    private final AnvilListener anvilListener = new AnvilListener();

    public AnvilFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isAnvil();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(anvilListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(anvilListener);
    }

}
