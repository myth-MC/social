package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;

@Feature(group = "social", identifier = "ANNOUNCEMENTS")
public final class AnnouncementsFeature {

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getAnnouncements().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getAnnouncementManager().restartTask();
    }

    @FeatureDisable
    public void disable() {
        Social.get().getAnnouncementManager().getAnnouncements().clear();
    }

}
