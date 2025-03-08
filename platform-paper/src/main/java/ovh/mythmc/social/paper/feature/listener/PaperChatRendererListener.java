package ovh.mythmc.social.paper.feature.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import net.kyori.adventure.identity.Identity;
import ovh.mythmc.gestalt.annotations.FeatureListener;
import ovh.mythmc.gestalt.features.FeatureEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.UserChatRenderer;
import ovh.mythmc.social.common.feature.ChatFeature;
import ovh.mythmc.social.paper.adapter.PaperChatEventAdapter;

public class PaperChatRendererListener {

    private SocialChatRenderer.Registered<BukkitSocialUser> renderer;

    private final PaperChatEventAdapter chatEventAdapter = new PaperChatEventAdapter();

    @FeatureListener(feature = ChatFeature.class, events = FeatureEvent.ENABLE)
    public void enable() {
        Bukkit.getPluginManager().registerEvents(chatEventAdapter, Bukkit.getPluginManager().getPlugin("social"));

        renderer = Social.get().getChatManager().registerRenderer(BukkitSocialUser.class, new UserChatRenderer<>(), options -> {
            return options
                .map(audience -> {
                    final var uuid = audience.get(Identity.UUID);
                    if (uuid.isEmpty())
                        return SocialChatRenderer.MapResult.ignore();

                    final var user = BukkitSocialUser.from(uuid.get());
                    if (user == null)
                        return SocialChatRenderer.MapResult.ignore();

                    return SocialChatRenderer.MapResult.success(user);
                });
        });
    }

    @FeatureListener(feature = ChatFeature.class, events = FeatureEvent.DISABLE)
    public void disable() {
        Social.get().getChatManager().unregisterRenderer(renderer);
        HandlerList.unregisterAll(chatEventAdapter);
    }
    
}
