package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.filters.URLFilter;

@Feature(group = "social", identifier = "URL_FILTER")
public final class URLFilterFeature {

    private final URLFilter urlFilter = new URLFilter();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().getFilter().isEnabled() &&
                Social.get().getConfig().getSettings().getChat().getFilter().isUrlFilter();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().registerContextualParser(urlFilter);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterContextualParser(urlFilter);
    }

}
