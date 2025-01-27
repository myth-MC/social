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
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.common.listeners.ChatListener;
import ovh.mythmc.social.common.wrappers.ChatEventWrapper;

@Feature(group = "social", identifier = "CHAT")
public final class ChatFeature {

    private final JavaPlugin plugin;

    private final ChatListener chatListener;

    public ChatFeature(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.chatListener = new ChatListener(plugin);
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Bukkit.getPluginManager().registerEvents(chatListener, plugin);
        Bukkit.getPluginManager().registerEvents(ChatEventWrapper.get(), plugin);

        // Assign channels to every SocialPlayer
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel != null) {
            Social.get().getUserManager().get().forEach(socialPlayer -> {
                Social.get().getChatManager().assignChannelsToPlayer(socialPlayer);
                Social.get().getUserManager().setMainChannel(socialPlayer, defaultChannel);
            });
        }
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(chatListener);
        HandlerList.unregisterAll(ChatEventWrapper.get());
        
        Social.get().getChatManager().getChannels().clear();
    }

}
