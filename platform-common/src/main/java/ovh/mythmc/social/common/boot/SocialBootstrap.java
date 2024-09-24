package ovh.mythmc.social.common.boot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.SocialSupplier;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.common.text.filters.IPFilter;
import ovh.mythmc.social.common.text.filters.URLFilter;
import ovh.mythmc.social.common.text.parsers.EmojiParser;
import ovh.mythmc.social.common.text.placeholders.impl.ChannelPlaceholder;
import ovh.mythmc.social.common.text.placeholders.impl.NicknamePlaceholder;
import ovh.mythmc.social.common.text.placeholders.impl.UsernamePlaceholder;

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

        try {
            enable();
        } catch (Throwable throwable) {
            getLogger().error("An error has occurred while initializing social: {}", throwable);
            throwable.printStackTrace(System.err);
            return;
        }

        // update checker
    }

    public abstract void enable();

    public abstract void shutdown();

    public final void reload() {
        // Stop running tasks

        // Clear channels, announcements, parsers, reactions and emojis (we don't want any duplicates)
        Social.get().getChatManager().getChannels().clear();
        Social.get().getAnnouncementManager().getAnnouncements().clear();
        Social.get().getTextProcessor().getParsers().clear();
        Social.get().getReactionManager().getReactions().clear();
        Social.get().getEmojiManager().getEmojis().clear();

        // Reload settings.yml and messages.yml
        getConfig().load();

        // Register internal placeholders
        Social.get().getTextProcessor().registerParser(
                new NicknamePlaceholder(),
                new ChannelPlaceholder(),
                new UsernamePlaceholder()
        );

        // Register internal filters
        if (Social.get().getConfig().getSettings().getFilter().isEnabled()) {
            if (Social.get().getConfig().getSettings().getFilter().isIpFilter())
                Social.get().getTextProcessor().registerParser(new IPFilter());

            if (Social.get().getConfig().getSettings().getFilter().isUrlFilter())
                Social.get().getTextProcessor().registerParser(new URLFilter());
        }

        // Register internal parsers
        if (Social.get().getConfig().getSettings().getEmojis().isEnabled())
            Social.get().getTextProcessor().registerParser(new EmojiParser());

        // Start all running tasks again
        Social.get().getAnnouncementManager().restartTask();

        // Assign channels to every SocialPlayer
        Social.get().getPlayerManager().get().forEach(socialPlayer -> Social.get().getChatManager().assignChannelsToPlayer(socialPlayer));
    }

    public abstract String version();

}
