package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.BooksListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class BooksFeature implements SocialFeature {

    private final BooksListener booksListener;

    public BooksFeature() {
        this.booksListener = new BooksListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.EMOJIS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getSettings().getTextReplacement().isBooks();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(booksListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(booksListener);
    }

}
