package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.ChatListener;
import ovh.mythmc.social.common.listeners.MOTDListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class MOTDFeature implements SocialFeature {

    private final MOTDListener motdListener;

    public MOTDFeature() {
        this.motdListener = new MOTDListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.MOTD;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getMotd().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(motdListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(motdListener);
    }

}
