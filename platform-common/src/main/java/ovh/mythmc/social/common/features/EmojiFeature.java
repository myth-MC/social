package ovh.mythmc.social.common.features;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.text.parsers.EmojiParser;
import ovh.mythmc.social.common.text.parsers.RawEmojiParser;

public final class EmojiFeature implements SocialFeature {

    private final EmojiParser emojiParser;
    private final RawEmojiParser rawEmojiParser;

    public EmojiFeature() {
        this.emojiParser = new EmojiParser();
        this.rawEmojiParser = new RawEmojiParser();
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
        Social.get().getTextProcessor().registerParser(emojiParser, rawEmojiParser);
    }

    @Override
    public void disable() {
        Social.get().getTextProcessor().unregisterParser(emojiParser, rawEmojiParser);
        Social.get().getEmojiManager().getEmojis().clear();
    }

}
