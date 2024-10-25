package ovh.mythmc.social.common.features;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listeners.AnvilListener;

@RequiredArgsConstructor
@Feature(key = "social", type = "EMOJIS")
public final class AnvilFeature {

    private final JavaPlugin plugin;

    private final AnvilListener anvilListener = new AnvilListener();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getSettings().getTextReplacement().isAnvil();
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
