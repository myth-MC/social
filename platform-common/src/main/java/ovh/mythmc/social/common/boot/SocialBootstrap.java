package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.database.SocialDatabase;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.callback.handler.InternalFeatureHandler;
import ovh.mythmc.social.common.command.SocialCommandProvider;
import ovh.mythmc.social.common.feature.AddonFeature;
import ovh.mythmc.social.common.feature.BootstrapFeature;
import ovh.mythmc.social.common.text.parser.MiniMessageParser;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholder.chat.ClickableChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.FormattedNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.prefix.*;

import java.io.File;

import org.incendo.cloud.CommandManager;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap implements Social {

    public static boolean isBrigadierAvailable() {
        try {
            Class.forName("com.mojang.brigadier.arguments.StringArgumentType");
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }

    private final SocialConfigProvider config;

    private final File dataDirectory;

    private SocialCommandProvider commandProvider;

    public SocialBootstrap(final File dataDirectory) {
        this.config = new SocialConfigProvider(dataDirectory);
        this.dataDirectory = dataDirectory;
    }

    public final void initialize() {
        SocialSupplier.set(this);

        // Initialize gestalt
        initializeGestalt();

        // Register bootstrap feature
        Gestalt.get().register(BootstrapFeature.class);

        // Register add-on feature (add-ons can listen to this class to sync their state)
        Gestalt.get().register(AddonFeature.class);

        // Register platform features (chat renderer, hooks...)
        registerPlatformFeatures();

        // Register internal listener
        Gestalt.get().getListenerRegistry().register(new InternalFeatureHandler());

        // Load settings
        reloadAll();

        // Register command manager
        commandProvider = new SocialCommandProvider(commandManager());

        try {
            // Enable plugin
            enable();

            // Register commands
            commandProvider.register();

            // Instantiate database
            SocialDatabase.newInstance(userType());

            // Initialize database
            SocialDatabase.get().initialize(dataDirectory.getAbsolutePath() + File.separator + "users.db");
        } catch (Throwable throwable) {
            Social.get().getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
        }
    }

    public abstract Class<? extends AbstractSocialUser> userType();

    public abstract void initializeGestalt();

    public abstract void registerPlatformFeatures();

    public abstract void unregisterPlatformFeatures();

    public abstract void enable();

    public abstract CommandManager<AbstractSocialUser> commandManager();

    public void shutdown() {
        SocialDatabase.get().shutdown();
    }

    public final void reload(ReloadType type) {
        switch (type) {
            case ADDONS:
                Gestalt.get().disableFeature(AddonFeature.class);
                Gestalt.get().enableFeature(AddonFeature.class);
                break;
            case SETTINGS:
                getConfig().loadSettings();
                break;
            case MESSAGES:
                getConfig().loadMessages();
                break;
            default:
                reloadAll();
                break;
        }
    }

    private final void reloadAll() {
        Gestalt.get().disableFeature(BootstrapFeature.class);

        // Disable add-ons
        Gestalt.get().disableFeature(AddonFeature.class);

        // Unregister all parsers
        Social.get().getTextProcessor().unregisterAllParsers();

        // Reload settings.yml and messages.yml
        getConfig().load();

        // Register internal placeholders
        Social.get().getTextProcessor().EARLY_PARSERS.add(
            new UsernamePlaceholder(),
            new NicknamePlaceholder(),
            new ClickableNicknamePlaceholder(),
            new FormattedNicknamePlaceholder()
        );

        Social.get().getTextProcessor().registerContextualParser(
                new ChannelPlaceholder(),
                new ChannelIconPlaceholder(),
                new ClickableChannelIconPlaceholder(),
                new SocialSpyPlaceholder()
        );

        // Register internal non-contextual placeholders
        Social.get().getTextProcessor().registerContextualParser(
                new ErrorPlaceholder(),
                new InfoPlaceholder(),
                new PrivateMessagePlaceholder(),
                new SuccessPlaceholder(),
                new WarningPlaceholder()
        );

        // Register parsers that do not necessarily belong to any feature group
        Social.get().getTextProcessor().LATE_PARSERS.add(
            new MiniMessageParser()
        );

        // Enable Gestalt features
        Gestalt.get().enableFeature(BootstrapFeature.class);;

        // Enable add-ons
        Gestalt.get().enableFeature(AddonFeature.class);
    }

    public abstract String version();

}
