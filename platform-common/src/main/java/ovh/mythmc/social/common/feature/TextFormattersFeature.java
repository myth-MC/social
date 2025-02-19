package ovh.mythmc.social.common.feature;

import java.util.List;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.formatter.SocialFormatter;
import ovh.mythmc.social.common.text.formatter.BoldTextFormatter;
import ovh.mythmc.social.common.text.formatter.ItalicTextFormatter;
import ovh.mythmc.social.common.text.formatter.ObfuscatedTextFormatter;
import ovh.mythmc.social.common.text.formatter.SmallFontTextFormatter;
import ovh.mythmc.social.common.text.formatter.StrikethroughTextFormatter;
import ovh.mythmc.social.common.text.formatter.UnderlineTextFormatter;

@Feature(group = "social", identifier = "TEXT_FORMATTER")
public final class TextFormattersFeature {

    private final List<SocialFormatter> formatters = List.of(
        new BoldTextFormatter(),
        new ItalicTextFormatter(),
        new ObfuscatedTextFormatter(),
        new SmallFontTextFormatter(),
        new StrikethroughTextFormatter(),
        new UnderlineTextFormatter()
    );

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isPlayerFormatOptions();
    }

    @FeatureEnable
    public void enable() {
        formatters.forEach(formatter -> Social.get().getTextProcessor().registerContextualParser(formatter));
    }

    @FeatureDisable
    public void disable() {
        formatters.forEach(formatter -> Social.get().getTextProcessor().unregisterContextualParser(formatter));
    }
    
}
