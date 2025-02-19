package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.update.UpdateChecker;

@Feature(group = "social", identifier = "UPDATE_CHECKER")
public final class UpdateCheckerFeature {

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getGeneral().isUpdateChecker();
    }

    @FeatureEnable
    public void enable() {
        if (!UpdateChecker.isRunning())
            UpdateChecker.startTask();
    }

}
