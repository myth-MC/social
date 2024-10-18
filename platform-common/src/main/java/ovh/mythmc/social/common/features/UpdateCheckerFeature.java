package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.update.UpdateChecker;

public final class UpdateCheckerFeature implements SocialFeature {

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.UPDATE_CHECKER;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().isUpdateChecker();
    }

    @Override
    public void enable() {
        if (!UpdateChecker.isRunning())
            UpdateChecker.startTask();
    }

    @Override
    public void disable() {
    }

}
