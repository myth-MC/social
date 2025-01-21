package ovh.mythmc.social.api.configuration;

import lombok.Getter;
import ovh.mythmc.social.api.Social;
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

@Getter
public final class ModernSocialSettings implements SocialSettings {

    private GeneralSettings general = Social.get().getConfig().getGeneral();

    private ChatSettings chat = Social.get().getConfig().getChat();

    private ReactionsSettings reactions = Social.get().getConfig().getReactions();

    private EmojiSettings emojis = Social.get().getConfig().getEmojis();

    private MOTDSettings motd = Social.get().getConfig().getMotd();

    private AnnouncementsSettings announcements = Social.get().getConfig().getAnnouncements();

    private SystemMessagesSettings systemMessages = Social.get().getConfig().getSystemMessages();

    private PacketsSettings packets = Social.get().getConfig().getPackets();

    private TextReplacementSettings textReplacement = Social.get().getConfig().getTextReplacement();

    private CommandsSettings commands = Social.get().getConfig().getCommands();

}
