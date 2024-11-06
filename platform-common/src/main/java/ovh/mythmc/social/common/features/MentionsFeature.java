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
import ovh.mythmc.social.common.listeners.MentionsListener;

@Feature(group = "social", identifier = "MENTIONS")
public final class MentionsFeature {

    private final JavaPlugin plugin;

    private final MentionsListener mentionsListener = new MentionsListener();

    public MentionsFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().isMentions();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(mentionsListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(mentionsListener);
    }

}
