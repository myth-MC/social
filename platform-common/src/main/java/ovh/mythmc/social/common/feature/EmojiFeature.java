package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.common.text.parser.EmojiParser;
import ovh.mythmc.social.common.text.parser.RawEmojiParser;

import java.util.List;

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
        List.copyOf(Social.registries().emojis().keys())
            .forEach(key -> Social.registries().emojis().unregister(key));
    }

}
