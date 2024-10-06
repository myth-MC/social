package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;

public final class AnnouncementsFeature implements SocialFeature {

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.ANNOUNCEMENTS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getAnnouncements().isEnabled();
    }

    @Override
    public void enable() {
        Social.get().getAnnouncementManager().restartTask();
    }

    @Override
    public void disable() {
        Social.get().getAnnouncementManager().getAnnouncements().clear();
    }

}
