package ovh.mythmc.social.common.features.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import github.scarsz.discordsrv.DiscordSRV;
import lombok.RequiredArgsConstructor;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.hooks.DiscordSRVDeathListener;
import ovh.mythmc.social.common.hooks.DiscordSRVHook;

@RequiredArgsConstructor
@Feature(group = "social", identifier = "HOOKS")
public final class DiscordSRVFeature {

    private final JavaPlugin plugin;

    private DiscordSRVHook hook = null;

    private DiscordSRVDeathListener deathListener = null;

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
    }

    @FeatureEnable
    public void enable() {
        this.hook = new DiscordSRVHook(plugin);
        this.deathListener = new DiscordSRVDeathListener();

        // Register Bukkit listeners
        Bukkit.getPluginManager().registerEvents(hook, plugin);

        // Register hook and subscribe to API events
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        DiscordSRV.api.subscribe(hook);

        // Register death listener if custom system messages are enabled
        if (Social.get().getConfig().getSettings().getSystemMessages().isEnabled() &&
            Social.get().getConfig().getSettings().getSystemMessages().isCustomizeDeathMessage()) {
            Bukkit.getPluginManager().registerEvents(deathListener, plugin);
        }
        
    }

    @FeatureDisable
    public void disable() {
        // Unregister Bukkit listeners
        HandlerList.unregisterAll(deathListener);
        HandlerList.unregisterAll(hook);

        // Unregister hook and unsuscribe from API events
        DiscordSRV.getPlugin().getPluginHooks().remove(hook);
        DiscordSRV.api.unsubscribe(hook);
    }
    
}
