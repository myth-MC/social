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
import ovh.mythmc.social.common.listeners.SignsListener;

@RequiredArgsConstructor
@Feature(group = "social", identifier = "EMOJIS")
public final class SignsFeature {

    private final JavaPlugin plugin;

    private final SignsListener signsListener = new SignsListener();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getSettings().getTextReplacement().isSigns();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(signsListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(signsListener);
    }

}
