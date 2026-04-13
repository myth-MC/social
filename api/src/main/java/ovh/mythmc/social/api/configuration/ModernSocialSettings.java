package ovh.mythmc.social.api.configuration;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.section.settings.*;

/**
 * Modern implementation of SocialSettings that pulls values from the active Social configuration.
 */
public final class ModernSocialSettings implements SocialSettings {

    private final GeneralSettings general = Social.get().getConfig().getGeneral();
    private final ChatSettings chat = Social.get().getConfig().getChat();
    private final ReactionsSettings reactions = Social.get().getConfig().getReactions();
    private final EmojiSettings emojis = Social.get().getConfig().getEmojis();
    private final MOTDSettings motd = Social.get().getConfig().getMotd();
    private final AnnouncementsSettings announcements = Social.get().getConfig().getAnnouncements();
    private final SystemMessagesSettings systemMessages = Social.get().getConfig().getSystemMessages();
    private final ServerLinksSettings serverLinks = Social.get().getConfig().getServerLinks();
    private final TextReplacementSettings textReplacement = Social.get().getConfig().getTextReplacement();
    private final CommandsSettings commands = Social.get().getConfig().getCommands();
    private final DatabaseSettings databaseSettings = Social.get().getConfig().getDatabaseSettings();

    @Override
    public @NotNull GeneralSettings getGeneral() {
        return general;
    }

    @Override
    public @NotNull ChatSettings getChat() {
        return chat;
    }

    @Override
    public @NotNull ReactionsSettings getReactions() {
        return reactions;
    }

    @Override
    public @NotNull EmojiSettings getEmojis() {
        return emojis;
    }

    @Override
    public @NotNull MOTDSettings getMotd() {
        return motd;
    }

    @Override
    public @NotNull AnnouncementsSettings getAnnouncements() {
        return announcements;
    }

    @Override
    public @NotNull SystemMessagesSettings getSystemMessages() {
        return systemMessages;
    }

    @Override
    public @NotNull ServerLinksSettings getServerLinks() {
        return serverLinks;
    }

    @Override
    public @NotNull TextReplacementSettings getTextReplacement() {
        return textReplacement;
    }

    @Override
    public @NotNull CommandsSettings getCommands() {
        return commands;
    }

    @Override
    public @NotNull DatabaseSettings getDatabaseSettings() {
        return databaseSettings;
    }

}

