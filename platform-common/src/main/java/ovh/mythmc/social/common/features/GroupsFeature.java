package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.api.text.parsers.SocialParser;
import ovh.mythmc.social.common.listeners.GroupsListener;
import ovh.mythmc.social.common.text.placeholders.groups.GroupIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupLeaderPlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupCodePlaceholder;
import ovh.mythmc.social.common.text.placeholders.groups.GroupPlaceholder;
import ovh.mythmc.social.common.util.PluginUtil;

import java.util.ArrayList;
import java.util.List;

public final class GroupsFeature implements SocialFeature {

    private final GroupsListener groupsListener;

    private final List<SocialParser> parsers = new ArrayList<>();

    public GroupsFeature() {
        this.groupsListener = new GroupsListener();
    }

    @Override
    public void initialize() {
        this.parsers.add(new GroupIconPlaceholder());
        this.parsers.add(new GroupLeaderPlaceholder());
        this.parsers.add(new GroupCodePlaceholder());
        this.parsers.add(new GroupPlaceholder());
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
        this.parsers.forEach(parser -> Social.get().getTextProcessor().registerParser(parser));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(groupsListener);
        this.parsers.forEach(parser -> Social.get().getTextProcessor().unregisterParser(parser));
    }

}
