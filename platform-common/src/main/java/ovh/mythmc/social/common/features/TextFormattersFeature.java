package ovh.mythmc.social.common.features;

import java.util.List;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.formatters.SocialFormatter;
import ovh.mythmc.social.common.text.formatters.BoldTextFormatter;
import ovh.mythmc.social.common.text.formatters.ItalicTextFormatter;
import ovh.mythmc.social.common.text.formatters.ObfuscatedTextFormatter;
import ovh.mythmc.social.common.text.formatters.SmallFontTextFormatter;
import ovh.mythmc.social.common.text.formatters.StrikethroughTextFormatter;
import ovh.mythmc.social.common.text.formatters.UnderlineTextFormatter;

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
        return Social.get().getConfig().getSettings().getChat().isPlayerFormatOptions();
    }

    @FeatureEnable
    public void enable() {
        formatters.forEach(formatter -> Social.get().getTextProcessor().registerParser(formatter));
    }

    @FeatureDisable
    public void disable() {
        formatters.forEach(formatter -> Social.get().getTextProcessor().unregisterParser(formatter));
    }
    
}
