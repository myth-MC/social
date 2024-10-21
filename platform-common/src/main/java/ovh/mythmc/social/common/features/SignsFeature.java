package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.SignsListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class SignsFeature implements SocialFeature {

    private final SignsListener signsListener;

    public SignsFeature() {
        this.signsListener = new SignsListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.EMOJIS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getTextReplacement().isEnabled() &&
                Social.get().getConfig().getSettings().getTextReplacement().isSigns();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(signsListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(signsListener);
    }

}
