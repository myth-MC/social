package ovh.mythmc.social.api.announcements;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.channel.ChatChannel;

final class AnnouncementImpl implements Announcement {

    private final Iterable<ChatChannel> channels;

    private final Component message;

    AnnouncementImpl(@NotNull Iterable<ChatChannel> channels, @NotNull Component message) {
        this.channels = channels;
        this.message = message;
    }

    public @NotNull Iterable<ChatChannel> channels() {
        return this.channels;
    }

    public @NotNull Component message() {
        return this.message;
    }

}
