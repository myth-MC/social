package ovh.mythmc.social.common.features;

import java.util.List;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionBoolean;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.features.FeaturePriority;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.sections.settings.EmojiSettings.EmojiField;

@Feature(group = "social", identifier = "MIGRATION", priority = FeaturePriority.HIGH)
public final class MigrationFeature {

    private final int currentMigrationVersion = 1;

    @FeatureConditionBoolean
    public boolean canBeEnabled() {
        return false;
        //return currentMigrationVersion != Social.get().getConfig().getSettings().getMigrationVersion();
    }

    @FeatureEnable
    public void enable() {
        // Update migration version
        Social.get().getConfig().getSettings().setMigrationVersion(currentMigrationVersion);

        // Update fields
        registerEmojis().forEach(emoji -> Social.get().getConfig().getSettings().getEmojis().getEmojis().add(emoji));

        // Disable migration feature
        disable();
    }

    @FeatureDisable
    public void disable() {
        // Save changes and reload
        Social.get().getConfig().save();
        // Social.get().reload();
    }

    private List<EmojiField> registerEmojis() {
        return List.of(
            new EmojiField("box_up_and_right", List.of(), "\u2514")
        );
    }
    
}
