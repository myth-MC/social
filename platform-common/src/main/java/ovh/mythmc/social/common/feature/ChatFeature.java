package ovh.mythmc.social.common.feature;

import java.util.ArrayList;
import java.util.Collection;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.ConsoleChatRenderer;
import ovh.mythmc.social.common.callback.handler.ChatHandler;

@Feature(group = "social", identifier = "CHAT")
public final class ChatFeature {

    private final ChatHandler chatHandler = new ChatHandler();

    private final Collection<SocialChatRenderer.Registered<?>> registeredRenderers = new ArrayList<>();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        chatHandler.register();

        // Assign channels to every user
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getChat().getDefaultChannel());
        if (defaultChannel != null) {
            Social.get().getUserService().get().forEach(user -> {
                Social.get().getChatManager().assignChannelsToPlayer(user);
                Social.get().getUserManager().setMainChannel(user, defaultChannel);
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
    }

    @FeatureDisable
    public void disable() {
        // Unregister handlers
        chatHandler.unregister();

        // Remove channels
        Social.get().getChatManager().getChannels().clear();

        // Unregister built-in renderers
        registeredRenderers.forEach(Social.get().getChatManager()::unregisterRenderer);
    }
    
}
