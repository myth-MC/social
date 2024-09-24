package ovh.mythmc.social.api.announcements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.configuration.sections.settings.AnnouncementsSettings;

import java.util.ArrayList;
import java.util.List;

public abstract class SocialAnnouncement {

    public abstract List<ChatChannel> channels();

    public abstract Component message();

    public static SocialAnnouncement fromConfigField(AnnouncementsSettings.Announcement announcementField) {
        return new SocialAnnouncement() {
            @Override
            public List<ChatChannel> channels() {
                List<ChatChannel> channels = new ArrayList<>();

                for (String channelName : announcementField.channels()) {
                    ChatChannel channel = Social.get().getChatManager().getChannel(channelName);
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
