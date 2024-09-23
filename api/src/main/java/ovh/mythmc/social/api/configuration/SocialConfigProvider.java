package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;

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

        // Register chat channels
        Social.get().getChatManager().getChannels().clear();
        settings.getChat().getChannels().forEach(channel -> {
            Social.get().getChatManager().registerChatChannel(ChatChannel.fromConfigField(channel));

            if (settings.isDebug())
                Social.get().getLogger().info("Registered channel '" + channel.name() + "'");
        });
    }

}
