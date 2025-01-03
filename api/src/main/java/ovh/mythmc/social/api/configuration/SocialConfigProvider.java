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
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.logger.LoggerWrapper;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.filters.SocialFilterLiteral;
import ovh.mythmc.social.api.text.filters.SocialFilterRegex;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Getter
public final class SocialConfigProvider {

    private final File pluginFolder;

    private SocialSettings settings;

    private SocialMessages messages;

    private SocialMenus menus;

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

        settings = new SocialSettings();
        messages = new SocialMessages();
        menus = new SocialMenus();
    }

    public void loadSettings() {
        settings = YamlConfigurations.update(
                new File(pluginFolder, "settings.yml").toPath(),
                SocialSettings.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );
    }

    public void loadMessages() {
        messages = YamlConfigurations.update(
                new File(pluginFolder, "messages.yml").toPath(),
                SocialMessages.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );
    }

    public void loadMenus() {
        menus = YamlConfigurations.update(
            new File(pluginFolder, "menus.yml").toPath(),
            SocialMenus.class,
            YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );
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
            settings.getChat().getFilter().getLiteralFilter().forEach(literal -> Social.get().getTextProcessor().registerParser(new SocialFilterLiteral() {
                @Override
                public String literal() {
                    return literal;
                }
            }));

            settings.getChat().getFilter().getCustomRegexFilter().forEach(regex -> Social.get().getTextProcessor().registerParser(new SocialFilterRegex() {
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

    public void save() {
        YamlConfigurations.save(new File(pluginFolder, "settings.yml").toPath(), SocialSettings.class, settings);
    }

    private Sound getSoundByKey(String key) {
        if (!Key.parseable(key)) {
            logger.warn("settings.yml contains an invalid key: " + key);
            return null;
        }

        return Sound.sound(Key.key(key), Sound.Source.PLAYER, 0.75f, 1.5f);
    }

}
