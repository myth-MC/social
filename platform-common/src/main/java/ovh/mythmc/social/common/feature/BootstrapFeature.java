package ovh.mythmc.social.common.feature;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.social.common.callback.handler.UserPresenceHandler;

@Feature(group = "social-bootstrap", identifier = "BOOTSTRAP")
public class BootstrapFeature {

    private static boolean initialized = false;

    private final UserPresenceHandler userPresenceHandler = new UserPresenceHandler();

    @FeatureInitialize
    public void initialize() {
        if (initialized)
            return;

        // Register features
        Gestalt.get().register(
            AddonFeature.class,
            AnnouncementsFeature.class,
            AnvilFeature.class,
            BookFeature.class,
            ChatFeature.class,
            CompanionFeature.class,
            EmojiFeature.class,
            GroupFeature.class,
            IPFilterFeature.class,
            MentionsFeature.class,
            MOTDFeature.class,
            ReactionsFeature.class,
            ServerLinksFeature.class,
            SignFeature.class,
            SystemMessagesFeature.class,
            TextFormattingFeature.class,
            UpdateCheckerFeature.class,
            URLFilterFeature.class
        );

        // User presence handler
        userPresenceHandler.register();

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
    
}
