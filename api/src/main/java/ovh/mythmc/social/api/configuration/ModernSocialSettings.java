package ovh.mythmc.social.api.configuration;

import lombok.Getter;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.section.settings.*;

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
