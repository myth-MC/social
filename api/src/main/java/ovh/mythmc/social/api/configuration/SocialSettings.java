package ovh.mythmc.social.api.configuration;

import ovh.mythmc.social.api.configuration.section.settings.AnnouncementsSettings;
import ovh.mythmc.social.api.configuration.section.settings.ChatSettings;
import ovh.mythmc.social.api.configuration.section.settings.CommandsSettings;
import ovh.mythmc.social.api.configuration.section.settings.DatabaseSettings;
import ovh.mythmc.social.api.configuration.section.settings.EmojiSettings;
import ovh.mythmc.social.api.configuration.section.settings.GeneralSettings;
import ovh.mythmc.social.api.configuration.section.settings.MOTDSettings;
import ovh.mythmc.social.api.configuration.section.settings.ReactionsSettings;
import ovh.mythmc.social.api.configuration.section.settings.ServerLinksSettings;
import ovh.mythmc.social.api.configuration.section.settings.SystemMessagesSettings;
import ovh.mythmc.social.api.configuration.section.settings.TextReplacementSettings;

public interface SocialSettings {

    GeneralSettings getGeneral();
    
    ChatSettings getChat();

    ReactionsSettings getReactions();

    EmojiSettings getEmojis();

    MOTDSettings getMotd();

    AnnouncementsSettings getAnnouncements();

    SystemMessagesSettings getSystemMessages();

    ServerLinksSettings getServerLinks();

    TextReplacementSettings getTextReplacement();

    CommandsSettings getCommands();

    DatabaseSettings getDatabaseSettings();

}
