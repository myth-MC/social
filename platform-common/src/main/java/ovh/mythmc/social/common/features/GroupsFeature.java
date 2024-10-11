package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.GroupsListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class GroupsFeature implements SocialFeature {

    private final GroupsListener groupsListener;

    public GroupsFeature() {
        this.groupsListener = new GroupsListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.GROUPS;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().isEnabled() &&
                Social.get().getConfig().getSettings().getChat().getGroups().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(groupsListener);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(groupsListener);
    }

}
