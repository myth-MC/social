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

public interface Social {

    @NotNull static Social get() { return SocialSupplier.get(); }

    @NotNull static SocialRegistries registries() { return SocialRegistries.INSTANCE; }

    @ApiStatus.Internal
    void reload(ReloadType type);

    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull SocialConfigProvider getConfig();

    @NotNull SocialUserService getUserService();

    @NotNull ReactionFactory getReactionFactory();

    @NotNull default AnnouncementManager getAnnouncementManager() { return AnnouncementManager.instance; }

    @NotNull default SocialUserManager getUserManager() { return SocialUserManager.instance; }

    @NotNull default ChatManager getChatManager() { return ChatManager.instance; }

    @NotNull default GlobalTextProcessor getTextProcessor() { return GlobalTextProcessor.instance; }

    enum ReloadType {
        ALL,
        ADDONS,
        SETTINGS,
        MESSAGES,
        MENUS
    }

}