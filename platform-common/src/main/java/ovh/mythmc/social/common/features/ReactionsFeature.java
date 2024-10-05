package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.ReactionsListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class ReactionsFeature implements SocialFeature {

    private final ReactionsListener reactionsListener;

    public ReactionsFeature() {
        this.reactionsListener = new ReactionsListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.REACTIONS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getReactions().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(reactionsListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(reactionsListener);
    }

}
