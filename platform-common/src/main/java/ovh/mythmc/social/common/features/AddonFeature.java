package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;

@Feature(group = "social", identifier = "ADDON")
public final class AddonFeature {

    @FeatureEnable
    public void enable() {
        Social.get().getLogger().info("Enabling add-ons...");
    }

    @FeatureDisable
    public void disable() {
        Social.get().getLogger().info("Disabling add-on...");
    }
    
}
