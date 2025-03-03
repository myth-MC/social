package ovh.mythmc.social.api.configuration;

import lombok.Getter;
import ovh.mythmc.social.api.Social;
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

@Getter
public final class ModernSocialSettings implements SocialSettings {

    private GeneralSettings general = Social.get().getConfig().getGeneral();

    private ChatSettings chat = Social.get().getConfig().getChat();

    private ReactionsSettings reactions = Social.get().getConfig().getReactions();

    private EmojiSettings emojis = Social.get().getConfig().getEmojis();

    private MOTDSettings motd = Social.get().getConfig().getMotd();

    private AnnouncementsSettings announcements = Social.get().getConfig().getAnnouncements();

    private SystemMessagesSettings systemMessages = Social.get().getConfig().getSystemMessages();

    private ServerLinksSettings serverLinks = Social.get().getConfig().getServerLinks();

    private TextReplacementSettings textReplacement = Social.get().getConfig().getTextReplacement();

    private CommandsSettings commands = Social.get().getConfig().getCommands();

    private DatabaseSettings databaseSettings = Social.get().getConfig().getDatabaseSettings();

}
