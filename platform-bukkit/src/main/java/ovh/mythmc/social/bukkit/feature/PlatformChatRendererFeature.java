package ovh.mythmc.social.bukkit.feature;

import net.kyori.adventure.identity.Identity;
import ovh.mythmc.gestalt.annotations.FeatureListener;
import ovh.mythmc.gestalt.features.FeatureEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.bukkit.SocialBukkit;
import ovh.mythmc.social.api.chat.renderer.SocialChatRenderer;
import ovh.mythmc.social.api.chat.renderer.defaults.UserChatRenderer;
import ovh.mythmc.social.common.feature.ChatFeature;

public final class PlatformChatRendererFeature {

    private SocialChatRenderer.Registered<BukkitSocialUser> renderer;

    @FeatureListener(feature = ChatFeature.class, events = FeatureEvent.ENABLE)
    public void enable() {
        renderer = Social.get().getChatManager().registerRenderer(BukkitSocialUser.class, new UserChatRenderer<>(), options -> {
            return options
                .map(audience -> {
                    final var uuid = audience.get(Identity.UUID);
                    if (uuid.isEmpty())
                        return SocialChatRenderer.MapResult.ignore();

                    final var user = SocialBukkit.get().getUserService().getByUuid(uuid.get());
                    if (user.isEmpty())
                        return SocialChatRenderer.MapResult.ignore();

                    return SocialChatRenderer.MapResult.success(user.get());
                });
        });
    }

    @FeatureListener(feature = ChatFeature.class, events = FeatureEvent.DISABLE)
    public void disable() {
        Social.get().getChatManager().unregisterRenderer(renderer);
    }
    
}
