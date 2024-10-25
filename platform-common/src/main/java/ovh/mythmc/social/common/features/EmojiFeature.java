package ovh.mythmc.social.common.features;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.parsers.EmojiParser;
import ovh.mythmc.social.common.text.parsers.RawEmojiParser;

@Feature(key = "social", type = "EMOJIS")
public final class EmojiFeature {

    private final EmojiParser emojiParser;
    private final RawEmojiParser rawEmojiParser;

    public EmojiFeature() {
        this.emojiParser = new EmojiParser();
        this.rawEmojiParser = new RawEmojiParser();
    }

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getEmojis().isEnabled();
    }

    @FeatureEnable
    public void enable() {
        Social.get().getTextProcessor().registerParser(emojiParser, rawEmojiParser);
    }

    @FeatureDisable
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(emojiParser, rawEmojiParser);
        Social.get().getEmojiManager().getEmojis().clear();
    }

}
