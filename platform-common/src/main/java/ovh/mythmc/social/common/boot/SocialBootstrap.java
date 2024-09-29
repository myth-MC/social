package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.common.text.filters.IPFilter;
import ovh.mythmc.social.common.text.filters.URLFilter;
import ovh.mythmc.social.common.text.parsers.EmojiParser;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelIconPlaceholder;
import ovh.mythmc.social.common.text.placeholders.chat.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.ClickableNicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.SocialSpyPlaceholder;
import ovh.mythmc.social.common.text.placeholders.player.UsernamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.prefix.*;
import ovh.mythmc.social.common.util.SchedulerUtil;

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
        reload();

        // Initialize scheduler
        SchedulerUtil.setPlugin((JavaPlugin) getPlugin());

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // Todo: update checker

        Bukkit.getPluginManager().callEvent(new SocialBootstrapEvent());

        // Register external plugin hooks
        hooks();
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void hooks() {
        // unused for now
    }

    public final void reload() {
        // Stop running tasks

        // Clear channels, announcements, parsers, reactions and emojis (we don't want any duplicates)
        Social.get().getChatManager().getChannels().clear();
        Social.get().getAnnouncementManager().getAnnouncements().clear();
        Social.get().getTextProcessor().getParsers().clear();
        Social.get().getReactionManager().getReactionsMap().clear();
        Social.get().getEmojiManager().getEmojis().clear();

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

        // Register internal filters
        if (Social.get().getConfig().getSettings().getChat().getFilter().isEnabled()) {
            if (Social.get().getConfig().getSettings().getChat().getFilter().isIpFilter())
                Social.get().getTextProcessor().registerParser(new IPFilter());

            if (Social.get().getConfig().getSettings().getChat().getFilter().isUrlFilter())
                Social.get().getTextProcessor().registerParser(new URLFilter());
        }

        // Register internal parsers
        if (Social.get().getConfig().getSettings().getEmojis().isEnabled())
            Social.get().getTextProcessor().registerParser(new EmojiParser());

        // Start all running tasks again
        Social.get().getAnnouncementManager().restartTask();

        // Assign channels to every SocialPlayer
        ChatChannel mainChannel = Social.get().getChatManager().getChannel(getConfig().getSettings().getChat().getDefaultChannel());
        Social.get().getPlayerManager().get().forEach(socialPlayer -> {
            Social.get().getChatManager().assignChannelsToPlayer(socialPlayer);
            socialPlayer.setMainChannel(mainChannel);
        });

        // Fire event if plugin is already enabled
        if (Bukkit.getPluginManager().isPluginEnabled("social"))
            Bukkit.getPluginManager().callEvent(new SocialBootstrapEvent());
    }

    public abstract String version();

}
