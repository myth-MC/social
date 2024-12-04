package ovh.mythmc.social.api.announcements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.channels.ChatChannel;
import ovh.mythmc.social.api.configuration.sections.settings.AnnouncementsSettings;

import java.util.ArrayList;
import java.util.List;

public abstract class Announcement {

    public abstract List<ChatChannel> channels();

    public abstract Component message();

    public static Announcement fromConfigField(AnnouncementsSettings.Announcement announcementField) {
        return new Announcement() {
            @Override
            public List<ChatChannel> channels() {
                List<ChatChannel> channels = new ArrayList<>();

                for (String channelName : announcementField.channels()) {
                    ChatChannel channel = Social.get().getChannelManager().getChannel(channelName);
                    if (channel != null)
                        channels.add(channel);
                }

                return channels;
            }

            @Override
            public Component message() {
                return MiniMessage.miniMessage().deserialize(announcementField.message());
            }
        };
    }

}
