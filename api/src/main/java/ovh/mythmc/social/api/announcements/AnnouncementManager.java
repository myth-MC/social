package ovh.mythmc.social.api.announcements;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class AnnouncementManager {

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    public static final AnnouncementManager instance = new AnnouncementManager();

    private int latest = 0;

    private final List<SocialAnnouncement> announcements = new ArrayList<>();

    public boolean registerAnnouncement(final @NotNull SocialAnnouncement announcement) {
        return announcements.add(announcement);
    }

    public boolean unregisterAnnouncement(final @NotNull SocialAnnouncement announcement) {
        return announcements.remove(announcement);
    }

    public void startTask() {
        asyncScheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SocialAnnouncement announcement = announcements.get(latest);
                for (ChatChannel channel : announcement.channels()) {
                    Collection<SocialPlayer> socialPlayers = new ArrayList<>();
                    channel.getMembers().forEach(uuid -> socialPlayers.add(Social.get().getPlayerManager().get(uuid)));

                    Social.get().getTextProcessor().send(socialPlayers, announcement.message());
                }

                latest = latest + 1;
                if (latest >= announcements.size())
                    latest = 0;
            }
        }, 0, Social.get().getConfig().getSettings().getAnnouncements().getFrequency(), TimeUnit.SECONDS);
    }

    public void stopTask() {
        asyncScheduler.close();
    }

}
