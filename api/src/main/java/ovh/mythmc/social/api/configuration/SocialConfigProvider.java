package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChatChannel;
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
import ovh.mythmc.social.api.emoji.Emoji;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.text.filter.SocialFilterLiteral;
import ovh.mythmc.social.api.text.filter.SocialFilterRegex;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Getter
public final class SocialConfigProvider {

    private final File pluginFolder;

    @Deprecated(since = "0.4", forRemoval = true) 
    private SocialSettings settings;

    private SocialMessages messages = new SocialMessages();

    private SocialMenus menus = new SocialMenus();

    // Individual files
    private GeneralSettings general = new GeneralSettings();

    private ChatSettings chat = new ChatSettings();

    private ReactionsSettings reactions = new ReactionsSettings();

    private EmojiSettings emojis = new EmojiSettings();

    private MOTDSettings motd = new MOTDSettings();

    private AnnouncementsSettings announcements = new AnnouncementsSettings();

    private SystemMessagesSettings systemMessages = new SystemMessagesSettings();

    private ServerLinksSettings serverLinks = new ServerLinksSettings();

    private TextReplacementSettings textReplacement = new TextReplacementSettings();

    private CommandsSettings commands = new CommandsSettings();

    private DatabaseSettings databaseSettings = new DatabaseSettings();

    static final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(String message, Object... args) {
            Social.get().getLogger().info("[config-provider] " + message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            Social.get().getLogger().warn("[config-provider] " + message, args);
        }

        @Override
        public void error(String message, Object... args) {
            Social.get().getLogger().error("[config-provider] " + message, args);
        }
    };

    public SocialConfigProvider(final @NotNull File pluginFolder) {
        this.pluginFolder = pluginFolder;
    }

    private <T> T updateSettingsFile(String fileName, Class<T> clazz) {
        return YamlConfigurations.update(
            new File(pluginFolder, "settings" + File.separator + fileName).toPath(),
            clazz,
            YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );
    }

    private <T> void saveSettingsFile(String fileName, Class<T> clazz, T newFile, SocialSettings settings) {
        if (settings instanceof LegacySocialSettings legacySocialSettings) {
            YamlConfigurations.save(
                new File(pluginFolder, "settings.yml").toPath(), 
                LegacySocialSettings.class, 
                legacySocialSettings);

            return;
        }

        YamlConfigurations.save(
            new File(pluginFolder, "settings" + File.separator + fileName).toPath(), 
            clazz, 
            newFile);
    }

    public void loadSettings() {
        if (Files.exists(new File(pluginFolder, "settings.yml").toPath())) {
            settings = new LegacySocialSettings();

            settings = YamlConfigurations.update(
                new File(pluginFolder, "settings.yml").toPath(),
                LegacySocialSettings.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
            );

            general = settings.getGeneral();
            chat = settings.getChat();
            reactions = settings.getReactions();
            emojis = settings.getEmojis();
            motd = settings.getMotd();
            announcements = settings.getAnnouncements();
            systemMessages = settings.getSystemMessages();
            serverLinks = settings.getServerLinks();
            textReplacement = settings.getTextReplacement();
            commands = settings.getCommands();
            databaseSettings = settings.getDatabaseSettings();

            logger.warn("This server is running an outdated settings file! Please, back up and delete your current settings.yml to regenerate a clean setup");
            return;
        }

        general = updateSettingsFile("general.yml", GeneralSettings.class);
        chat = updateSettingsFile("chat.yml", ChatSettings.class);
        reactions = updateSettingsFile("reactions.yml", ReactionsSettings.class);
        emojis = updateSettingsFile("emojis.yml", EmojiSettings.class);
        motd = updateSettingsFile("motd.yml", MOTDSettings.class);
        announcements = updateSettingsFile("announcements.yml", AnnouncementsSettings.class);
        systemMessages = updateSettingsFile("system-messages.yml", SystemMessagesSettings.class);
        serverLinks = updateSettingsFile("server-links.yml", ServerLinksSettings.class);
        textReplacement = updateSettingsFile("text-replacement.yml", TextReplacementSettings.class);
        commands = updateSettingsFile("commands.yml", CommandsSettings.class);
        databaseSettings = updateSettingsFile("database.yml", DatabaseSettings.class);

        settings = new ModernSocialSettings();
    }

    public void loadMessages() {
        messages = YamlConfigurations.update(
                new File(pluginFolder, "messages.yml").toPath(),
                SocialMessages.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );
    }

    public void loadMenus() {
        menus = updateSettingsFile("menus.yml", SocialMenus.class);
    }

    public void load() {
        loadSettings();
        loadMessages();
        loadMenus();

        // Register emojis
        settings.getEmojis().getEmojis().forEach(emojiField -> {
            Emoji emoji = new Emoji(emojiField.name(), emojiField.aliases(), emojiField.unicodeCharacter());
            Social.get().getEmojiManager().registerEmoji("server", emoji);
        });

        // Register custom placeholders
        if (settings.getChat().getFilter().isEnabled()) {
            settings.getChat().getFilter().getLiteralFilter().forEach(literal -> Social.get().getTextProcessor().registerContextualParser(new SocialFilterLiteral() {
                @Override
                public String literal() {
                    return literal;
                }
            }));

            settings.getChat().getFilter().getCustomRegexFilter().forEach(regex -> Social.get().getTextProcessor().registerContextualParser(new SocialFilterRegex() {
                @Override
                public String regex() {
                    return regex;
                }
            }));
        }

        // Register reactions
        settings.getReactions().getReactions().forEach(reactionField -> {
            Reaction reaction;
            if (reactionField.sound() != null) {
                reaction = new Reaction(reactionField.name(), reactionField.texture(), getSoundByKey(reactionField.sound()), reactionField.triggerWords());
            } else {
                reaction = new Reaction(reactionField.name(), reactionField.texture(), null, reactionField.triggerWords());
            }
            Social.get().getReactionManager().registerReaction("SERVER", reaction);
        });

        // Register chat channels
        settings.getChat().getChannels().forEach(channel -> Social.get().getChatManager().registerChatChannel(ChatChannel.fromConfigField(channel)));

        // Register announcements
        settings.getAnnouncements().getMessages().forEach(announcementField -> {
            SocialAnnouncement announcement = SocialAnnouncement.fromConfigField(announcementField);
            Social.get().getAnnouncementManager().registerAnnouncement(announcement);
        });
    }

    private Sound getSoundByKey(String key) {
        if (!Key.parseable(key)) {
            logger.warn("reactions.yml contains an invalid key: " + key);
            return null;
        }

        return Sound.sound(Key.key(key), Sound.Source.PLAYER, 0.75f, 1.5f);
    }

    public void updateVersion(int newVersion) {
        this.settings.getDatabaseSettings().setVersion(newVersion);
        saveSettingsFile("database.yml", DatabaseSettings.class, this.settings.getDatabaseSettings(), this.settings);
    }

}
