package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.social.api.user.SocialUserCompanion;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.common.callback.handler.CompanionHandler;

@Feature(group = "social", identifier = "COMPANION")
public final class CompanionFeature {

    private final CompanionHandler companionHandler = new CompanionHandler();

    @FeatureInitialize
    public void initialize() {
        SocialUserCompanion.S2C_CHANNELS.forEach(channelName -> {
            PlatformAdapter.get().registerS2CPayloadChannel(channelName);
        });

        SocialUserCompanion.C2S_CHANNELS.forEach(channelName -> {
            PlatformAdapter.get().registerC2SPayloadChannel(channelName);
        });
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
