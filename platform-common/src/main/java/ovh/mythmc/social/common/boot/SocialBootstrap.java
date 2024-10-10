package ovh.mythmc.social.common.boot;

import github.scarsz.discordsrv.DiscordSRV;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.api.features.SocialGestalt;
import ovh.mythmc.social.common.features.*;
import ovh.mythmc.social.common.hooks.DiscordSRVHook;
import ovh.mythmc.social.common.hooks.PlaceholderAPIHook;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.prefix.*;
import ovh.mythmc.social.common.util.PluginUtil;

import java.io.File;

@Getter
@RequiredArgsConstructor
public abstract class SocialBootstrap<T> implements Social {

    private T plugin;
    private SocialConfigProvider config;

    public SocialBootstrap(final @NotNull T plugin,
                           final File dataDirectory) {
        SocialSupplier.set(this);

        this.plugin = plugin;
        this.config = new SocialConfigProvider(dataDirectory);
    }

    public final void initialize() {
        // Initialize gestalt
        SocialGestalt.set(new SocialGestalt());
        SocialGestalt.get().registerFeature(
                new AnnouncementsFeature(),
                new ChatFeature(),
                new EmojiFeature(),
                new IPFilterFeature(),
                new MentionsFeature(),
                new MOTDFeature(),
                new PacketsFeature(),
                new ReactionsFeature(),
                new SystemMessagesFeature(),
                new URLFilterFeature()
        );

        // Initialize scheduler and various utilities
        PluginUtil.setPlugin((JavaPlugin) getPlugin());

        // Load settings
        reload();

        try {
            // Register external plugin hooks
            hooks();
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // Todo: update checker

        Bukkit.getPluginManager().callEvent(new SocialBootstrapEvent());
    }

    public abstract void enable();

    public abstract void shutdown();

    // Todo: move to features and completely remove internal hooks?
    public final void hooks() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PlaceholderAPIHook placeholderAPIHook = new PlaceholderAPIHook();
            Social.get().getInternalHookManager().registerHooks(placeholderAPIHook);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            DiscordSRVHook discordSRVHook = new DiscordSRVHook(DiscordSRV.getPlugin());
            Social.get().getInternalHookManager().registerHooks(discordSRVHook);
        }
    }

    public final void reload() {
        SocialGestalt.get().disableAllFeatures();

        // Clear parsers
        Social.get().getTextProcessor().getParsers().clear();

        // Reload settings.yml and messages.yml
        getConfig().load();

        // Register internal placeholders
        Social.get().getTextProcessor().registerParser(
                new ClickableNicknamePlaceholder(),
                new NicknamePlaceholder(),
                new ChannelPlaceholder(),
                new ChannelIconPlaceholder(),
                new SocialSpyPlaceholder(),
                new UsernamePlaceholder()
        );

        // Register internal non-contextual placeholders
        Social.get().getTextProcessor().registerParser(
                new ErrorPlaceholder(),
                new InfoPlaceholder(),
                new PrivateMessagePlaceholder(),
                new SuccessPlaceholder(),
                new WarningPlaceholder()
        );

        // Fire event if plugin is already enabled
        if (Bukkit.getPluginManager().isPluginEnabled("social"))
            Bukkit.getPluginManager().callEvent(new SocialBootstrapEvent());

        // Enable Gestalt features
        SocialGestalt.get().enableAllFeatures();
    }

    public abstract String version();

}
