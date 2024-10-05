package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.text.filters.IPFilter;

public final class IPFilterFeature implements SocialFeature {

    private final IPFilter ipFilter;

    public IPFilterFeature() {
        this.ipFilter = new IPFilter();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.IP_FILTER;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().getFilter().isEnabled() &&
                Social.get().getConfig().getSettings().getChat().getFilter().isIpFilter();
    }

    @Override
    public void enable() {
        Social.get().getTextProcessor().registerParser(ipFilter);
    }

    @Override
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(ipFilter);
    }

}
