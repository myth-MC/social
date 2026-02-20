package ovh.mythmc.social.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.announcements.AnnouncementManager;
import ovh.mythmc.social.api.chat.ChatManager;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.api.text.GlobalTextProcessor;
import ovh.mythmc.social.api.user.SocialUserManager;
import ovh.mythmc.social.api.user.SocialUserService;

/**
 * The central entry-point for the social plugin API.
 *
 * <p>
 * Use {@link #get()} to retrieve the running instance and {@link #registries()}
 * to
 * access the shared registries for channels, announcements, emojis and
 * reactions.
 */
public interface Social {

    /**
     * Returns the active {@link Social} instance.
     *
     * @return the social API instance
     */
    @NotNull
    static Social get() {
        return SocialSupplier.get();
    }

    /**
     * Returns the shared {@link SocialRegistries} that hold all registered
     * channels, announcements, emojis and reactions.
     *
     * @return the global registries
     */
    @NotNull
    static SocialRegistries registries() {
        return SocialRegistries.INSTANCE;
    }

    /**
     * Triggers an internal reload of the specified type.
     *
     * @param type the parts of the plugin to reload
     */
    @ApiStatus.Internal
    void reload(ReloadType type);

    /**
     * Returns the current version string of the social plugin.
     *
     * @return version string
     */
    String version();

    /**
     * Returns the logger used throughout the plugin.
     *
     * @return the logger wrapper
     */
    @NotNull
    LoggerWrapper getLogger();

    /**
     * Returns the configuration provider that exposes all plugin settings.
     *
     * @return the config provider
     */
    @NotNull
    SocialConfigProvider getConfig();

    /**
     * Returns the service used to retrieve all currently tracked
     * {@link ovh.mythmc.social.api.user.AbstractSocialUser} instances.
     *
     * @return the user service
     */
    @NotNull
    SocialUserService getUserService();

    /**
     * Returns the factory responsible for creating and managing chat reactions.
     *
     * @return the reaction factory
     */
    @NotNull
    ReactionFactory getReactionFactory();

    /**
     * Returns the singleton {@link AnnouncementManager}.
     *
     * @return the announcement manager
     */
    @NotNull
    default AnnouncementManager getAnnouncementManager() {
        return AnnouncementManager.instance;
    }

    /**
     * Returns the singleton {@link SocialUserManager}.
     *
     * @return the user manager
     */
    @NotNull
    default SocialUserManager getUserManager() {
        return SocialUserManager.instance;
    }

    /**
     * Returns the singleton {@link ChatManager}.
     *
     * @return the chat manager
     */
    @NotNull
    default ChatManager getChatManager() {
        return ChatManager.instance;
    }

    /**
     * Returns the singleton {@link GlobalTextProcessor} used to parse and send
     * messages.
     *
     * @return the global text processor
     */
    @NotNull
    default GlobalTextProcessor getTextProcessor() {
        return GlobalTextProcessor.instance;
    }

    /**
     * Describes which parts of the plugin are affected by a reload operation.
     */
    enum ReloadType {
        /** Reload everything. */
        ALL,
        /** Reload third-party addon integrations only. */
        ADDONS,
        /** Reload configuration settings only. */
        SETTINGS,
        /** Reload user-facing message strings only. */
        MESSAGES,
        /** Reload menu layouts only. */
        MENUS
    }

}