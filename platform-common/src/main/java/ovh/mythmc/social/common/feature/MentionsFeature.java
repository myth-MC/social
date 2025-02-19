package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.listener.MentionsListener;

@Feature(group = "social", identifier = "MENTIONS")
public final class MentionsFeature {

    private final MentionsListener mentionsListener = new MentionsListener();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isMentions();
    }

    @FeatureEnable
    public void enable() {
        mentionsListener.registerCallbackHandlers();
    }

    @FeatureDisable
    public void disable() {
        mentionsListener.unregisterCallbackHandlers();
    }

}
