package ovh.mythmc.social.bukkit.feature.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.bukkit.hook.PlaceholderAPIHook;

@Feature(group = "social", identifier = "ADDON")
public final class PlaceholderAPIFeature {

    private PlaceholderAPIHook hook;

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @FeatureEnable
    public void enable() {
        this.hook = new PlaceholderAPIHook();

        // Register parser and events
        Social.get().getTextProcessor().registerContextualParser(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);

        // Register PlaceholderAPI expansion
        hook.getExpansion().register();
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterContextualParser(hook);
        HandlerList.unregisterAll(hook);

        hook.getExpansion().unregister();
    }
    
}
