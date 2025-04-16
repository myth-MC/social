package ovh.mythmc.social.api.announcements;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.configuration.section.settings.AnnouncementsSettings;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.Arrays;

public final class Announcement {

    public static Announcement of(@NotNull Component message, @NotNull Iterable<ChatChannel> channels) {
        return new Announcement(channels, message);
    }

    public static Announcement of(@NotNull Component message, @NotNull ChatChannel... channels) {
        return new Announcement(Arrays.asList(channels), message);
    }

    private final Iterable<ChatChannel> channels;

    private final Component message;

    private Announcement(@NotNull Iterable<ChatChannel> channels, @NotNull Component message) {
        this.channels = channels;
        this.message = message;
    }

    public Iterable<ChatChannel> channels() {
        return this.channels;
    }

    public Component message() {
        return this.message;
    }

    public static Announcement fromConfigField(AnnouncementsSettings.Announcement announcementField) {
        return of(
            Component.text(announcementField.message()),
            announcementField.channels().stream()
                .map(channelName -> Social.registries().channels().value(RegistryKey.identified(channelName)).orElse(null))
                .toList()
        );
    }

}
