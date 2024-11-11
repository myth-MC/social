package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.features.FeatureConstructorParams;
import ovh.mythmc.gestalt.features.GestaltFeature;
import ovh.mythmc.gestalt.loader.BukkitGestaltLoader;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.common.features.*;
import ovh.mythmc.social.common.features.hooks.DiscordSRVFeature;
import ovh.mythmc.social.common.features.hooks.PlaceholderAPIFeature;
import ovh.mythmc.social.common.listeners.InternalFeatureListener;
import ovh.mythmc.social.common.text.parsers.MiniMessageParser;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.prefix.*;
import ovh.mythmc.social.common.update.UpdateChecker;

import java.io.File;
import java.util.Arrays;

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

        // Register gestalt features
        Gestalt.get().register(
            AnnouncementsFeature.class,
            EmojiFeature.class,
            IPFilterFeature.class,
            UpdateCheckerFeature.class,
            URLFilterFeature.class
        );

        // Register features that require a plugin instance
        registerFeatureWithPluginParam(
            AnvilFeature.class,
            BooksFeature.class,
            ChatFeature.class,
            GroupsFeature.class,
            MentionsFeature.class,
            MOTDFeature.class,
            PacketsFeature.class,
            ReactionsFeature.class,
            ServerLinksFeature.class,
            SignsFeature.class,
            SystemMessagesFeature.class,
            DiscordSRVFeature.class,
            PlaceholderAPIFeature.class);

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

        UpdateChecker.startTask();
    }

    public abstract void enable();

    public void shutdown() {
        gestalt.terminate();
    }

    private void registerFeatureWithPluginParam(Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            Gestalt.get().register(GestaltFeature.builder()
                .featureClass(clazz)
                .constructorParams(FeatureConstructorParams.builder()
                    .params(plugin)
                    .types(JavaPlugin.class)
                    .build())
                .build());
        });
    }

    public final void reload() {
        Gestalt.get().disableAllFeatures("social");

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
                //new ClickableNicknamePlaceholder(),
                //new NicknamePlaceholder(),
                new ChannelPlaceholder(),
                new ChannelIconPlaceholder(),
                new SocialSpyPlaceholder()
                //new UsernamePlaceholder()
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
        Gestalt.get().enableAllFeatures("social");
    }

    public abstract String version();

}
