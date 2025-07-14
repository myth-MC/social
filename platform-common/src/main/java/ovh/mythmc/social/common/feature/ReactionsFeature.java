package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionVersion;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.ReactionHandler;

import java.util.List;

@Feature(group = "social", identifier = "REACTIONS")
@FeatureConditionVersion(versions = "1.21")
public final class ReactionsFeature {

    private final ReactionHandler reactionHandler = new ReactionHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getReactions().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        reactionHandler.register();
    }

    @FeatureDisable
    public void disable() {
        reactionHandler.unregister();
        List.copyOf(Social.registries().reactions().keys())
            .forEach(Social.registries().reactions()::unregister);
    }

}