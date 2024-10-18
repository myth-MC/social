package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.AnvilListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class AnvilFeature implements SocialFeature {

    private final AnvilListener anvilListener;

    public AnvilFeature() {
        this.anvilListener = new AnvilListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.EMOJIS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getSettings().getTextReplacement().isAnvil();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(anvilListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(anvilListener);
    }

}
