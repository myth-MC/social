package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.MOTDHandler;

@Feature(group = "social", identifier = "MOTD")
public final class MOTDFeature {

    private final MOTDHandler motdHandler = new MOTDHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getMotd().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        motdHandler.register();
    }

    @FeatureDisable
    public void disable() {
        motdHandler.unregister();
    }
    
}
