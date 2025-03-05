package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.callback.handler.BookEditHandler;

@Feature(group = "social", identifier = "EMOJIS")
public final class BookFeature {
    
    private final BookEditHandler bookEditHandler = new BookEditHandler();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getTextReplacement().isBooks();
    }

    @FeatureEnable
    public void enable() {
        bookEditHandler.register();
    }

    @FeatureDisable
    public void disable() {
        bookEditHandler.unregister();
    }

}
