package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.ServerLinksListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class ServerLinksFeature implements SocialFeature {

    private final ServerLinksListener serverLinksListener;

    public ServerLinksFeature() {
        this.serverLinksListener = new ServerLinksListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.SERVER_LINKS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getPackets().isEnabled() &&
                Social.get().getConfig().getSettings().getPackets().getServerLinks().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(serverLinksListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(serverLinksListener);
    }

}
