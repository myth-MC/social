package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.common.text.parser.URLParser;

@Feature(group = "social", identifier = "CLICKABLE_URLS")
public class ClickableURLFeature {

    private final SocialContextualParser parser = new URLParser();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isClickableUrls();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().LATE_PARSERS.add(parser);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().LATE_PARSERS.remove(parser);
    }

}
