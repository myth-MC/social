package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.SystemMessagesListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class SystemMessagesFeature implements SocialFeature {

    private final SystemMessagesListener systemMessagesListener;

    public SystemMessagesFeature() {
        this.systemMessagesListener = new SystemMessagesListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.SYSTEM_MESSAGES;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getSystemMessages().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(systemMessagesListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(systemMessagesListener);
    }

}
