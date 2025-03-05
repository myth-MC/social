package ovh.mythmc.social.common.feature;

import java.util.List;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.parser.SocialContextualParser;
import ovh.mythmc.social.common.callback.handler.GroupHandler;
import ovh.mythmc.social.common.text.placeholder.group.GroupCodePlaceholder;
import ovh.mythmc.social.common.text.placeholder.group.GroupIconPlaceholder;
import ovh.mythmc.social.common.text.placeholder.group.GroupLeaderPlaceholder;
import ovh.mythmc.social.common.text.placeholder.group.GroupPlaceholder;

@Feature(group = "social", identifier = "GROUP")
public final class GroupFeature {

    private final GroupHandler groupHandler = new GroupHandler();

    private final List<SocialContextualParser> parsers = List.of(
        new GroupIconPlaceholder(),
        new GroupLeaderPlaceholder(),
        new GroupCodePlaceholder(),
        new GroupPlaceholder()
    );

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return Social.get().getConfig().getChat().isEnabled() &&
                Social.get().getConfig().getChat().getGroups().isEnabled();
    }
    
    @FeatureEnable
    public void enable() {
        groupHandler.register();

        this.parsers.forEach(Social.get().getTextProcessor()::registerContextualParser);
    }

    @FeatureDisable
    public void disable() {
        groupHandler.unregister();

        this.parsers.forEach(Social.get().getTextProcessor()::unregisterContextualParser);
    }

}
