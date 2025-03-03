package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.database.SocialDatabase;
import ovh.mythmc.social.common.feature.AddonFeature;
import ovh.mythmc.social.common.feature.BootstrapFeature;
import ovh.mythmc.social.common.listener.InternalFeatureListener;
import ovh.mythmc.social.common.text.parser.MiniMessageParser;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelNicknameColorPlaceholder;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholder.chat.ChannelTextColorPlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.FormattedNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholder.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholder.prefix.*;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap<T> implements Social {

    private final JavaPlugin plugin;
    private final SocialConfigProvider config;

    private final File dataDirectory;

    public SocialBootstrap(final @NotNull JavaPlugin plugin,
                           final File dataDirectory) {
        SocialSupplier.set(this);

        this.plugin = plugin;
        this.config = new SocialConfigProvider(dataDirectory);
        
        this.dataDirectory = dataDirectory;
    }

    public final void initialize() {
        // Initialize gestalt
        initializeGestalt();

        // Register bootstrap feature
        Gestalt.get().register(BootstrapFeature.class);

        // Register add-on feature (add-ons can listen to this class to sync their state)
        Gestalt.get().register(AddonFeature.class);

        // Register internal listener
        Gestalt.get().getListenerRegistry().register(new InternalFeatureListener());

        // Load settings
        reloadAll();

        try {
            // Enable plugin
            enable();

            // Initialize database
            Logger.setGlobalLogLevel(Level.ERROR); // Disable unnecessary verbose
            SocialDatabase.get().initialize(dataDirectory.getAbsolutePath() + File.separator + "users.db");
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }
    }

    public abstract void initializeGestalt();

    public abstract void enable();

    public void shutdown() {
        SocialDatabase.get().shutdown();
    }

    @Override
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
                new ChannelNicknameColorPlaceholder(),
                new ChannelTextColorPlaceholder(),
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
