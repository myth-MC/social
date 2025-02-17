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
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;
import ovh.mythmc.social.common.listeners.GroupsListener;
import ovh.mythmc.social.common.text.placeholders.groups.GroupIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupLeaderPlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupCodePlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupPlaceholder;

import java.util.List;

@Feature(group = "social", identifier = "GROUPS")
public final class GroupsFeature {

    private final JavaPlugin plugin;

    private final GroupsListener groupsListener = new GroupsListener();

    private final List<SocialContextualParser> parsers = List.of(
        new GroupIconPlaceholder(),
        new GroupLeaderPlaceholder(),
        new GroupCodePlaceholder(),
        new GroupPlaceholder()
    );

    public GroupsFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isEnabled() &&
                Social.get().getConfig().getChat().getGroups().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(groupsListener, plugin);
        groupsListener.registerCallbackHandlers();

        this.parsers.forEach(Social.get().getTextProcessor()::registerContextualParser);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(groupsListener);
        groupsListener.unregisterCallbackHandlers();
        
        this.parsers.forEach(Social.get().getTextProcessor()::unregisterContextualParser);
    }

}
