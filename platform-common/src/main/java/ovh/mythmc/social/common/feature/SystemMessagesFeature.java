package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.SystemMessageHandler;

@Feature(group = "social", identifier = "SYSTEM_MESSAGES")
public final class SystemMessagesFeature {

    private final SystemMessageHandler systemMessageHandler = new SystemMessageHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSystemMessages().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        systemMessageHandler.register();
    }

    @FeatureDisable
    public void disable() {
        systemMessageHandler.unregister();
    }
    
}
