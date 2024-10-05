package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.text.parsers.EmojiParser;

public final class EmojiFeature implements SocialFeature {

    private final EmojiParser emojiParser;

    public EmojiFeature() {
        this.emojiParser = new EmojiParser();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.EMOJIS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getEmojis().isEnabled();
    }

    @Override
    public void enable() {
        Social.get().getTextProcessor().registerParser(emojiParser);
    }

    @Override
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(emojiParser);
    }

}
