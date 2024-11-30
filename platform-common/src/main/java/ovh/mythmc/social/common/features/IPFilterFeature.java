package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.filters.IPFilter;

@Feature(group = "social", identifier = "IP_FILTER")
public final class IPFilterFeature {

    private final IPFilter ipFilter = new IPFilter();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().getFilter().isEnabled() &&
                Social.get().getConfig().getSettings().getChat().getFilter().isIpFilter();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().registerParser(ipFilter);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(ipFilter);
    }

}
