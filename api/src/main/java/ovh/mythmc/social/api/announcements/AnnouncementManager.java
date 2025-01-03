package ovh.mythmc.social.api.announcements;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

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

    private boolean running = false;

    private final List<SocialAnnouncement> announcements = new ArrayList<>();

    public boolean registerAnnouncement(final @NotNull SocialAnnouncement announcement) {
        return announcements.add(announcement);
    }

    public boolean unregisterAnnouncement(final @NotNull SocialAnnouncement announcement) {
        return announcements.remove(announcement);
    }

    private void performTask() {
        if (!Social.get().getConfig().getSettings().getAnnouncements().isEnabled())
            return;

        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                SocialAnnouncement announcement = announcements.get(latest);

                if (Social.get().getConfig().getSettings().getAnnouncements().isUseActionBar()) {
                    Social.get().getPlayerManager().get().forEach(socialPlayer -> {
                        SocialParserContext context = SocialParserContext.builder()
                            .socialPlayer(socialPlayer)
                            .message(announcement.message())
                            .messageChannelType(ChannelType.ACTION_BAR)
                            .build();

                        Social.get().getTextProcessor().parseAndSend(context);
                    });
                } else {
                    for (ChatChannel channel : announcement.channels()) {
                        channel.getMembers().forEach(uuid -> {
                            SocialParserContext context = SocialParserContext.builder()
                                .socialPlayer(Social.get().getPlayerManager().get(uuid))
                                .message(announcement.message())
                                .messageChannelType(channel.getType())
                                .build();

                            Social.get().getTextProcessor().parseAndSend(context);
                        });
                    }
                }

                latest = latest + 1;
                if (latest >= announcements.size())
                    latest = 0;

                performTask();
            }
        }, Social.get().getConfig().getSettings().getAnnouncements().getFrequency(), TimeUnit.SECONDS);
    }

    public void restartTask() {
        if (running)
            return;

        running = true;
        performTask();
    }

}
