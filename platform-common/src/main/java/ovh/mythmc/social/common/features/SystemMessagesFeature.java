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
import ovh.mythmc.social.common.listeners.SystemMessagesListener;

@Feature(group = "social", identifier = "SYSTEM_MESSAGES")
public final class SystemMessagesFeature {

    private final JavaPlugin plugin;

    private final SystemMessagesListener systemMessagesListener;

    public SystemMessagesFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.systemMessagesListener = new SystemMessagesListener(plugin);
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSystemMessages().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(systemMessagesListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(systemMessagesListener);
    }

}
