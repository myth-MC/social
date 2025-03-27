package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.social.api.network.channel.channels.SocialPayloadChannels;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.callback.handler.CompanionHandler;

@Feature(group = "social", identifier = "COMPANION")
public final class CompanionFeature {

    private final CompanionHandler companionHandler = new CompanionHandler();

    @FeatureInitialize
    public void initialize() {
        SocialPayloadChannels.getAll().forEach(PlatformAdapter.get()::registerPayloadChannel);
    }

    @FeatureEnable
    public void enable() {
        companionHandler.register();
    }

    @FeatureDisable
    public void disable() {
        companionHandler.unregister();
    }
    
}
