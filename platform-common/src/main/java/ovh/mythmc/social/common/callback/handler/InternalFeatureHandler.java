package ovh.mythmc.social.common.callback.handler;

import ovh.mythmc.gestalt.annotations.FeatureListener;
import ovh.mythmc.gestalt.features.FeatureEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import ovh.mythmc.social.api.util.registry.RegistryKey;

public final class InternalFeatureHandler {

    @FeatureListener(group = "social", identifier = "REACTIONS", events = { FeatureEvent.ENABLE })
    public void onReactionsEnable() {
        final var reaction = Reaction.builder("social", "http://textures.minecraft.net/texture/7faf072f1692f795b19b7862879829aff55709673ed821c23cf0018ef04a26aa")
            .sound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 0.75f, 1.5f))
            .triggerWords("chirp")
            .build();

        final var registryKey = RegistryKey.namespaced("hidden", "birb");
        Social.registries().reactions().register(registryKey, reaction);
    }

}
