package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.filter.IPFilter;

@Feature(group = "social", identifier = "IP_FILTER")
public final class IPFilterFeature {

    private final IPFilter ipFilter = new IPFilter();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().getFilter().isEnabled() &&
                Social.get().getConfig().getChat().getFilter().isIpFilter();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().registerContextualParser(ipFilter);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterContextualParser(ipFilter);
    }

}
