package ovh.mythmc.social.common.listeners;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.api.reactions.Reaction;

import java.util.List;

public final class SocialBootstrapListener implements Listener {

    @EventHandler
    public void onSocialBootstrap(SocialBootstrapEvent event) {
        // Hidden reaction
        Social.get().getReactionManager().registerReaction("hidden", new Reaction("social", "http://textures.minecraft.net/texture/7faf072f1692f795b19b7862879829aff55709673ed821c23cf0018ef04a26aa", Sound.ENTITY_EXPERIENCE_ORB_PICKUP, List.of("chirp")));
    }

}
