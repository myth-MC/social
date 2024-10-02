package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.text.filters.URLFilter;

public final class URLFilterFeature implements SocialFeature {

    private final URLFilter urlFilter;

    public URLFilterFeature() {
        this.urlFilter = new URLFilter();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.URL_FILTER;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().getFilter().isEnabled() &&
                Social.get().getConfig().getSettings().getChat().getFilter().isUrlFilter();
    }

    @Override
    public void enable() {
        Social.get().getTextProcessor().registerParser(urlFilter);
    }

    @Override
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(urlFilter);
    }

}
