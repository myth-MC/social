package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.parsers.EmojiParser;
import ovh.mythmc.social.common.text.parsers.RawEmojiParser;

@Feature(group = "social", identifier = "EMOJIS")
public final class EmojiFeature {

    private final EmojiParser emojiParser = new EmojiParser();
    private final RawEmojiParser rawEmojiParser = new RawEmojiParser();

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getEmojis().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().LATE_PARSERS.add(emojiParser, rawEmojiParser);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().LATE_PARSERS.remove(emojiParser, rawEmojiParser);
        Social.get().getEmojiManager().unregisterAll();
    }

}
