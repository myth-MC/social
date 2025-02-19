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
import ovh.mythmc.social.common.listener.BooksListener;

@Feature(group = "social", identifier = "EMOJIS")
public final class BooksFeature {

    private final JavaPlugin plugin;

    private final BooksListener booksListener = new BooksListener();

    public BooksFeature(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isBooks();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(booksListener, plugin);
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(booksListener);
    }

}
