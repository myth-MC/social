package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.announcements.SocialAnnouncement;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.text.filters.SocialFilterLiteral;
import ovh.mythmc.social.api.text.filters.SocialFilterRegex;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

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

        // Register chat channels
        Social.get().getChatManager().getChannels().clear();
        settings.getChat().getChannels().forEach(channel -> {
            Social.get().getChatManager().registerChatChannel(ChatChannel.fromConfigField(channel));

            if (settings.isDebug())
                Social.get().getLogger().info("Registered channel '" + channel.name() + "'");
        });

        // Register custom placeholders
        if (settings.getFilter().isEnabled()) {
            settings.getFilter().getLiteralFilter().forEach(literal -> Social.get().getTextProcessor().registerParser(new SocialFilterLiteral() {
                @Override
                public String literal() {
                    return literal;
                }
            }));

            settings.getFilter().getCustomRegexFilter().forEach(regex -> Social.get().getTextProcessor().registerParser(new SocialFilterRegex() {
                @Override
                public String regex() {
                    return regex;
                }
            }));
        }

        // Register announcements
        Social.get().getAnnouncementManager().getAnnouncements().clear();
        settings.getAnnouncements().getMessages().forEach(announcementField -> {
            SocialAnnouncement announcement = SocialAnnouncement.fromConfigField(announcementField);
            Social.get().getAnnouncementManager().registerAnnouncement(announcement);
        });
    }

}
