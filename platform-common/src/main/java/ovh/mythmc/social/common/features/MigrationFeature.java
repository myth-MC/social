package ovh.mythmc.social.common.features;

import java.util.List;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.sections.settings.EmojiSettings.EmojiField;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureProperties;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.api.features.SocialFeatureProperties.Priority;

@SocialFeatureProperties(priority = Priority.HIGH)
public final class MigrationFeature implements SocialFeature {

    private final int currentMigrationVersion = 1;

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.OTHER;
    }

    @Override
    public boolean canBeEnabled() {
        return currentMigrationVersion != Social.get().getConfig().getSettings().getMigrationVersion();
    }

    @Override
    public void enable() {
        // Update migration version
        Social.get().getConfig().getSettings().setMigrationVersion(currentMigrationVersion);

        // Update fields
        registerEmojis().forEach(emoji -> Social.get().getConfig().getSettings().getEmojis().getEmojis().add(emoji));

        // Disable migration feature
        disable();
    }

    @Override
    public void disable() {
        // Save changes and reload
        Social.get().getConfig().save();
        Social.get().reload();
    }

    private List<EmojiField> registerEmojis() {
        return List.of(
            new EmojiField("box_up_and_right", List.of(), "\u2514")
        );
    }
    
}
