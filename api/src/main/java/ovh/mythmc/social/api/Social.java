package ovh.mythmc.social.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.announcements.AnnouncementManager;
import ovh.mythmc.social.api.chat.ChatManager;
import ovh.mythmc.social.api.configuration.SocialConfigProvider;
import ovh.mythmc.social.api.emojis.EmojiManager;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reactions.ReactionManager;
import ovh.mythmc.social.api.text.GlobalTextProcessor;
import ovh.mythmc.social.api.players.SocialPlayerManager;

public interface Social {

    @NotNull static Social get() { return SocialSupplier.get(); }

    @ApiStatus.Internal
    void reload();

    String version();

    @NotNull LoggerWrapper getLogger();

    @NotNull
    SocialConfigProvider getConfig();

    @NotNull default AnnouncementManager getAnnouncementManager() { return AnnouncementManager.instance; }

    @NotNull default SocialPlayerManager getPlayerManager() { return SocialPlayerManager.instance; }

    @NotNull default ChatManager getChatManager() { return ChatManager.instance; }

    @NotNull default GlobalTextProcessor getTextProcessor() { return GlobalTextProcessor.instance; }

    @NotNull default ReactionManager getReactionManager() { return ReactionManager.instance; }

    @NotNull default EmojiManager getEmojiManager() { return EmojiManager.instance; }

}