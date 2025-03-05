package ovh.mythmc.social.api.announcements;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;

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
        if (!Social.get().getConfig().getAnnouncements().isEnabled())
            return;

        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                SocialAnnouncement announcement = announcements.get(latest);

                if (Social.get().getConfig().getAnnouncements().isUseActionBar()) {
                    Social.get().getUserService().get().forEach(user -> {
                        SocialParserContext context = SocialParserContext.builder(user, announcement.message())
                            .messageChannelType(ChannelType.ACTION_BAR)
                            .build();

                        Social.get().getTextProcessor().parseAndSend(context);
                    });
                } else {
                    for (ChatChannel channel : announcement.channels()) {
                        channel.getMembers().forEach(user -> {
                            SocialParserContext context = SocialParserContext.builder(user, announcement.message())
                                .build();

                            Component component = Social.get().getTextProcessor().parse(context);
                            Social.get().getTextProcessor().send(user, component, ChannelType.CHAT, channel);
                        });
                    }
                }

                latest++;
                if (latest >= announcements.size())
                    latest = 0;

                performTask();
            }
        }, Social.get().getConfig().getAnnouncements().getFrequency(), TimeUnit.SECONDS);
    }

    public void restartTask() {
        if (running)
            return;

        running = true;
        performTask();
    }

}
