package ovh.mythmc.social.api.announcements;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.context.SocialParserContext;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnouncementManager {

    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(1);

    public static final AnnouncementManager instance = new AnnouncementManager();

    private int index = 0;

    private boolean running = false;

    private void performTask() {
        if (!Social.get().getConfig().getAnnouncements().isEnabled())
            return;

        asyncScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
            final var announcementRegistry = Social.registries().announcements();
            final Announcement announcement = announcementRegistry.values().get(index);

            if (Social.get().getConfig().getAnnouncements().isUseActionBar()) {
                Social.get().getUserService().get().forEach(user -> {
                    final SocialParserContext context = SocialParserContext.builder(user, announcement.message())
                        .messageChannelType(ChatChannel.ChannelType.ACTION_BAR)
                        .build();

                    Social.get().getTextProcessor().parseAndSend(context);
                });
            } else {
                for (ChatChannel channel : announcement.channels()) {
                    channel.members().forEach(user -> {
                        final SocialParserContext context = SocialParserContext.builder(user, announcement.message())
                            .build();

                        final Component component = Social.get().getTextProcessor().parse(context);
                        Social.get().getTextProcessor().send(user, component, ChatChannel.ChannelType.CHAT, channel);
                    });
                }
            }

            index++;
            if (index >= announcementRegistry.values().size())
                index = 0;

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
