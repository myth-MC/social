package ovh.mythmc.social.common.feature;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listener.SignsListener;

@Feature(group = "social", identifier = "EMOJIS")
public final class SignsFeature {

    private final JavaPlugin plugin;

    private SignsListener signsListener = new SignsListener();

    public SignsFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isSigns();
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
