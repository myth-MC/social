package ovh.mythmc.social.bukkit.feature.hook;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import github.scarsz.discordsrv.DiscordSRV;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.scheduler.SocialScheduler;
import ovh.mythmc.social.bukkit.hook.DiscordSRVDeathListener;
import ovh.mythmc.social.bukkit.hook.DiscordSRVHook;

@Feature(group = "social", identifier = "ADDON")
public class DiscordSRVFeature {

    private DiscordSRVHook hook;

    private DiscordSRVDeathListener deathListener;

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("social");

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isEnabled() &&
            Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
    }

    @FeatureEnable
    public void enable() {
        SocialScheduler.get().runGlobalTask(() -> {
            this.hook = new DiscordSRVHook(plugin);
            this.deathListener = new DiscordSRVDeathListener();

            // Register hook and subscribe to API events
            DiscordSRV.getPlugin().getPluginHooks().add(hook);
            DiscordSRV.api.subscribe(hook);
    
            // Register callback handler
            hook.registerMessageCallbackHandler();
    
            // Register death listener if custom system messages are enabled
            if (Social.get().getConfig().getSystemMessages().isEnabled() &&
                Social.get().getConfig().getSystemMessages().isCustomizeDeathMessage()) {
                    Bukkit.getPluginManager().registerEvents(deathListener, plugin);
            }
            
        });
    }

    @FeatureDisable
    public void disable() {
        // Unregister Bukkit listeners
        HandlerList.unregisterAll(deathListener);
        HandlerList.unregisterAll(hook);

        DiscordSRV.getPlugin().getPluginHooks().remove(hook);
        DiscordSRV.api.unsubscribe(hook);

        // Unregister callback handler
        hook.unregisterMessageCallbackHandler();
    }
    
}
