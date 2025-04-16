package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;

import java.util.List;

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
        List.copyOf(Social.registries().announcements().keys())
            .forEach(key -> Social.registries().announcements().unregister(key));
    }

}
