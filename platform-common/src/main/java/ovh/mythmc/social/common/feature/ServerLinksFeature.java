package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionVersion;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.ServerLinksHandler;

@Feature(group = "social", identifier = "SERVER_LINKS")
@FeatureConditionVersion(versions = "1.21")
public final class ServerLinksFeature {

    private final ServerLinksHandler serverLinksHandler = new ServerLinksHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getServerLinks().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        serverLinksHandler.register();
    }

    @FeatureDisable
    public void disable() {
        serverLinksHandler.unregister();
    }
    
}
