package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class SocialSettingsProvider {

    private SocialSettings settings;

    private final Path settingsFilePath;

    public SocialSettingsProvider(final @NotNull File pluginFolder) {
        this.settings = new SocialSettings();
        this.settingsFilePath = new File(pluginFolder, "settings.yml").toPath();
    }

    public void load() {
        this.settings = YamlConfigurations.update(
                settingsFilePath,
                SocialSettings.class,
                YamlConfigurationProperties.newBuilder().charset(StandardCharsets.UTF_8).build()
        );

        // Chat channels
        settings.getChat().getChannels().forEach(channel -> {
            Social.get().getChatManager().registerChatChannel(ChatChannel.fromConfigField(channel));

            if (settings.isDebug())
                Social.get().getLogger().info("Registered channel '" + channel.name() + "'");
        });
    }

    public SocialSettings get() { return settings; }

}
