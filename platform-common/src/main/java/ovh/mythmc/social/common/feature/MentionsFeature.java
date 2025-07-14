package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.MentionHandler;

@Feature(group = "social", identifier = "MENTIONS")
public final class MentionsFeature {

    private final MentionHandler mentionHandler = new MentionHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isMentions();
    }

    @FeatureEnable
    public void enable() {
        mentionHandler.registerCallbackHandlers();
    }

    @FeatureDisable
    public void disable() {
        mentionHandler.unregisterCallbackHandlers();
    }

}