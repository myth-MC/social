package ovh.mythmc.social.common.features.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.hooks.PlaceholderAPIHook;

@RequiredArgsConstructor
@Feature(group = "social", identifier = "HOOKS")
public final class PlaceholderAPIFeature {

    private final JavaPlugin plugin;

    private PlaceholderAPIHook hook = null;

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    @FeatureEnable
    public void enable() {
        this.hook = new PlaceholderAPIHook();
        
        // Register parser and events
        Social.get().getTextProcessor().registerParser(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);

        // Register PlaceholderAPI expansion
        hook.getExpansion().register();
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(hook);
        HandlerList.unregisterAll(hook);

        hook.getExpansion().unregister();
    }
    
}
