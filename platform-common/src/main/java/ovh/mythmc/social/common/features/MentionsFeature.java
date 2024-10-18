package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.MentionsListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class MentionsFeature implements SocialFeature {

    private final MentionsListener mentionsListener;

    public MentionsFeature() {
        this.mentionsListener = new MentionsListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.MENTIONS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().isMentions();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(mentionsListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(mentionsListener);
    }

}
