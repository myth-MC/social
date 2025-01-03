package ovh.mythmc.social.common.features;

import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.common.commands.SocialCommandManager;

@Feature(group = "social", identifier = "commands")
public final class CommandsFeature {

    private final SocialCommandManager commandManager;

    public CommandsFeature(JavaPlugin plugin) {
        commandManager = new SocialCommandManager(plugin);
    }

    @FeatureEnable
    public void enable() {
        commandManager.registerArguments();
        commandManager.registerMessages();
        commandManager.registerRequirements();
        commandManager.registerSuggestions();
        commandManager.registerCommands();
    }

    @FeatureDisable
    public void disable() {
        commandManager.unregisterCommands();
    }
    
}
