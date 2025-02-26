package ovh.mythmc.social.common.feature;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.features.FeatureConstructorParams;
import ovh.mythmc.gestalt.features.GestaltFeature;
import ovh.mythmc.social.common.feature.hook.DiscordSRVFeature;
import ovh.mythmc.social.common.feature.hook.PlaceholderAPIFeature;

@Feature(group = "social", identifier = "BOOTSTRAP")
public final class BootstrapFeature {

    private static boolean initialized = false;

    @FeatureInitialize
    public void initialize() {
        if (initialized)
            return;

        // Register gestalt features
        Gestalt.get().register(
            AnnouncementsFeature.class,
            EmojiFeature.class,
            IPFilterFeature.class,
            MentionsFeature.class,
            TextFormattersFeature.class,
            UpdateCheckerFeature.class,
            URLFilterFeature.class
        );

        // Register features that require a plugin instance
        registerFeatureWithPluginParam(
            AnvilFeature.class,
            BooksFeature.class,
            ChatFeature.class,
            CommandsFeature.class,
            GroupsFeature.class,
            MOTDFeature.class,
            CompanionFeature.class,
            ReactionsFeature.class,
            ServerLinksFeature.class,
            SignsFeature.class,
            SystemMessagesFeature.class,
            DiscordSRVFeature.class,
            PlaceholderAPIFeature.class);

        initialized = true;
    }

    @FeatureEnable
    public void enable() {
        Gestalt.get().enableAllFeatures("social");
    }

    @FeatureDisable
    public void disable() {
        Gestalt.get().disableAllFeatures("social");
    }

    private static void registerFeatureWithPluginParam(Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            Gestalt.get().register(GestaltFeature.builder()
                .featureClass(clazz)
                .constructorParams(FeatureConstructorParams.builder()
                    .params((JavaPlugin) Bukkit.getPluginManager().getPlugin("social")) // works for now but it needs to be changed asap
                    .types(JavaPlugin.class)
                    .build())
                .build());
        });
    }

}
