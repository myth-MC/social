package ovh.mythmc.social.common.features;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.ConsoleChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.UserChatRenderer;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.adapters.ChatEventAdapter;
import ovh.mythmc.social.common.listeners.ChatListener;

@Feature(group = "social", identifier = "CHAT")
public final class ChatFeature {

    private final JavaPlugin plugin;

    private final ChatListener chatListener;

    private final Collection<SocialChatRenderer.Registered<?>> registeredRenderers = new ArrayList<>();

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
        Bukkit.getPluginManager().registerEvents(ChatEventAdapter.get(), plugin);

        // Register callback handlers
        chatListener.registerCallbackHandlers();

        // Assign channels to every SocialPlayer
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel != null) {
            Social.get().getUserManager().get().forEach(socialPlayer -> {
                Social.get().getChatManager().assignChannelsToPlayer(socialPlayer);
                Social.get().getUserManager().setMainChannel(socialPlayer, defaultChannel);
            });
        }

        // Register built-in console renderer
        registeredRenderers.add(
            Social.get().getChatManager().registerRenderer(Audience.class, new ConsoleChatRenderer(), options -> {
                return options
                    .map(audience -> {
                        var console = SocialAdventureProvider.get().console();
                        if (console.getClass().isInstance(audience))
                            return SocialChatRenderer.MapResult.success(console);

                        return SocialChatRenderer.MapResult.ignore();
                    });
                })
        );

        // Register built-in user/player renderer
        registeredRenderers.add(
            Social.get().getChatManager().registerRenderer(SocialUser.class, new UserChatRenderer(), options -> {
                return options
                    .map(audience -> {
                        var uuid = audience.get(Identity.UUID);
                        if (uuid.isEmpty())
                            return SocialChatRenderer.MapResult.ignore();
    
                        var user = Social.get().getUserManager().getByUuid(uuid.get());
                        if (user == null)
                            return SocialChatRenderer.MapResult.ignore();
    
                        return SocialChatRenderer.MapResult.success(user);
                    });
            })
        );
    }

    @FeatureDisable
    public void disable() {
        HandlerList.unregisterAll(chatListener);
        HandlerList.unregisterAll(ChatEventAdapter.get());

        // Unregister callback handlers
        chatListener.unregisterCallbackHandlers();

        // Remove all channels
        Social.get().getChatManager().getChannels().clear();

        // Unregister built-in renderers
        registeredRenderers.forEach(Social.get().getChatManager()::unregisterRenderer);
    }

}
