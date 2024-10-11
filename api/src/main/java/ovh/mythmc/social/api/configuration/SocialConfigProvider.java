package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.text.filters.SocialFilterLiteral;
import ovh.mythmc.social.api.text.filters.SocialFilterRegex;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class SocialConfigProvider {

    @Getter
    private SocialSettings settings;
    private final Path settingsFilePath;

    @Getter
    private SocialMessages messages;
    private final Path messagesFilePath;

    public SocialConfigProvider(final @NotNull File pluginFolder) {
        this.settings = new SocialSettings();
        this.messages = new SocialMessages();
        this.settingsFilePath = new File(pluginFolder, "settings.yml").toPath();
        this.messagesFilePath = new File(pluginFolder, "messages.yml").toPath();
    }

    public void load() {
        this.settings = YamlConfigurations.update(
                settingsFilePath,
                SocialSettings.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );

        this.messages = YamlConfigurations.update(
                messagesFilePath,
                SocialMessages.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );

        // Register emojis
        settings.getEmojis().getEmojis().forEach(emojiField -> {
            Emoji emoji = new Emoji(emojiField.name(), emojiField.aliases(), emojiField.unicodeCharacter());
            Social.get().getEmojiManager().registerEmoji(emoji);
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
                reaction = new Reaction(reactionField.name(), reactionField.texture(), findByName(reactionField.sound()), reactionField.triggerWords());
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

    private Sound findByName(String name) {
        Sound result = null;
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(name)) {
                result = sound;
                break;
            }
        }
        return result;
    }

}
