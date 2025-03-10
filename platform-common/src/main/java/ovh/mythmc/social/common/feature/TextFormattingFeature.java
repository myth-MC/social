package ovh.mythmc.social.common.feature;

import java.util.Collection;
import java.util.List;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.common.text.parser.TextFormattingParser;

@Feature(group = "social", identifier = "TEXT_FORMATTING")
public final class TextFormattingFeature {

    private final Collection<SocialContextualParser> parsers = List.of(
        new TextFormattingParser()
    );

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isPlayerFormatOptions();
    }

    @FeatureEnable
    public void enable() {
        parsers.forEach(Social.get().getTextProcessor()::registerContextualParser);
    }

    @FeatureDisable
    public void disable() {
        parsers.forEach(Social.get().getTextProcessor()::registerContextualParser);
    }
    
}
