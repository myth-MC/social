package ovh.mythmc.social.common.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.common.text.parsers.PlaceholderAPIParser;

import java.util.List;

public final class SocialBootstrapListener implements Listener {

    @EventHandler
    public void onSocialBootstrap(SocialBootstrapEvent event) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            new PlaceholderAPIParser();

        // Hidden reaction
        Social.get().getReactionManager().registerReaction("hidden", new Reaction("social", "http://textures.minecraft.net/texture/7faf072f1692f795b19b7862879829aff55709673ed821c23cf0018ef04a26aa", Sound.ENTITY_PARROT_FLY, List.of("chirp")));
    }

}
