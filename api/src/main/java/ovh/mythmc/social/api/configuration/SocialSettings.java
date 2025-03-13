package ovh.mythmc.social.api.configuration;

import ovh.mythmc.social.api.configuration.section.settings.*;

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
