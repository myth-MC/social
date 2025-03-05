package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.SignEditHandler;

@Feature(group = "social", identifier = "EMOJIS")
public final class SignFeature {

    private final SignEditHandler signEditHandler = new SignEditHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isSigns();
    }

    @FeatureEnable
    public void enable() {
        signEditHandler.register();
    }

    @FeatureDisable
    public void disable() {
        signEditHandler.unregister();
    }
    
}
