package ovh.mythmc.social.api.configuration;

import ovh.mythmc.social.api.configuration.sections.settings.AnnouncementsSettings;
import ovh.mythmc.social.api.configuration.sections.settings.ChatSettings;
import ovh.mythmc.social.api.configuration.sections.settings.CommandsSettings;
import ovh.mythmc.social.api.configuration.sections.settings.EmojiSettings;
import ovh.mythmc.social.api.configuration.sections.settings.GeneralSettings;
import ovh.mythmc.social.api.configuration.sections.settings.MOTDSettings;
import ovh.mythmc.social.api.configuration.sections.settings.PacketsSettings;
import ovh.mythmc.social.api.configuration.sections.settings.ReactionsSettings;
import ovh.mythmc.social.api.configuration.sections.settings.SystemMessagesSettings;
import ovh.mythmc.social.api.configuration.sections.settings.TextReplacementSettings;

public interface SocialSettings {

    GeneralSettings getGeneral();
    
    ChatSettings getChat();

    ReactionsSettings getReactions();

    EmojiSettings getEmojis();

    MOTDSettings getMotd();

    AnnouncementsSettings getAnnouncements();

    SystemMessagesSettings getSystemMessages();

    PacketsSettings getPackets();

    TextReplacementSettings getTextReplacement();

    CommandsSettings getCommands();

}
