package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.loader.BukkitGestaltLoader;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.common.features.BootstrapFeature;
import ovh.mythmc.social.common.listeners.InternalFeatureListener;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.prefix.*;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap<T> implements Social {

    private final JavaPlugin plugin;
    private final SocialConfigProvider config;

    private final BukkitGestaltLoader gestalt;

    public SocialBootstrap(final @NotNull JavaPlugin plugin,
                           final File dataDirectory) {
        SocialSupplier.set(this);

        this.plugin = plugin;
        this.config = new SocialConfigProvider(dataDirectory);
        this.gestalt = BukkitGestaltLoader.builder().initializer(plugin).build();
    }

    public final void initialize() {
        // Initialize gestalt
        gestalt.initialize();

        Gestalt.get().register(BootstrapFeature.class);
        Gestalt.get().getListenerRegistry().register(new InternalFeatureListener());

        // Load settings
        reload();

        try {
            // Enable plugin
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }
    }

    public abstract void enable();

    public void shutdown() {
        gestalt.terminate();
    }

    public final void reload() {
        Gestalt.get().disableFeature(BootstrapFeature.class);

        // Clear parsers
        Social.get().getTextProcessor().getParsers().clear();

        // Reload settings.yml and messages.yml
        getConfig().load();

        // Register internal placeholders
        Social.get().getTextProcessor().EARLY_PARSERS.add(
            new UsernamePlaceholder(),
            new NicknamePlaceholder(),
            new ClickableNicknamePlaceholder()
        );

        Social.get().getTextProcessor().registerParser(
                new ChannelPlaceholder(),
                new ChannelIconPlaceholder(),
                new SocialSpyPlaceholder()
        );

        // Register internal non-contextual placeholders
        Social.get().getTextProcessor().registerParser(
                new ErrorPlaceholder(),
                new InfoPlaceholder(),
                new PrivateMessagePlaceholder(),
                new SuccessPlaceholder(),
                new WarningPlaceholder()
        );

        // Register parsers that do not necessarily belong to any feature group
        Social.get().getTextProcessor().registerParser(
            new MiniMessageParser()
        );

        // Enable Gestalt features
        Gestalt.get().enableFeature(BootstrapFeature.class);;
    }

    public abstract String version();

}
