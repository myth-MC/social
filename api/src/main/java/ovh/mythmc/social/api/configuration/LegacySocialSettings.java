package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.configuration.section.settings.*;

/**
 * Legacy configuration implementation using ConfigLib annotations.
 */
@Configuration
public final class LegacySocialSettings implements SocialSettings {

    public boolean nagAdmins = true;

    public boolean isNagAdmins() {
        return nagAdmins;
    }

    @Comment({"", "General settings"})
    private GeneralSettings general = new GeneralSettings();

    @Comment({"", "Chat module"})
    private ChatSettings chat = new ChatSettings();

    @Comment({"", "Reactions module"})
    private ReactionsSettings reactions = new ReactionsSettings();

    @Comment({"", "Emojis module"})
    private EmojiSettings emojis = new EmojiSettings();

    @Comment({"", "MOTD module"})
    private MOTDSettings motd = new MOTDSettings();

    @Comment({"", "Announcements module"})
    private AnnouncementsSettings announcements = new AnnouncementsSettings();

    @Comment({"", "System messages module"})
    private SystemMessagesSettings systemMessages = new SystemMessagesSettings();

    @Comment({"", "Server links module"})
    private ServerLinksSettings serverLinks = new ServerLinksSettings();

    @Comment({"", "Text replacement module"})
    private TextReplacementSettings textReplacement = new TextReplacementSettings();

    @Comment({"", "Commands settings"})
    private CommandsSettings commands = new CommandsSettings();

    @Comment({"", "Database settings"})
    private DatabaseSettings databaseSettings = new DatabaseSettings();

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

