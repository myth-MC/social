package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionVersion;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.AnvilRenameHandler;

@Feature(group = "social", identifier = "EMOJIS")
@FeatureConditionVersion(versions = "1.21")
public final class AnvilFeature {

    private final AnvilRenameHandler anvilRenameHandler = new AnvilRenameHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isAnvil();
    }

    @FeatureEnable
    public void enable() {
        anvilRenameHandler.register();
    }

    @FeatureDisable
    public void disable() {
        anvilRenameHandler.unregister();
    }

}
