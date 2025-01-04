package ovh.mythmc.social.api.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPreSwitchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();
    private static final List<SocialUser> playerList = new ArrayList<>();

    public @NotNull List<SocialUser> get() {
        return List.copyOf(playerList);
    }

    public SocialUser get(final @NotNull UUID uuid) {
        for (SocialUser player : playerList) {
            if (player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    public void registerSocialPlayer(final @NotNull SocialUser socialPlayer) {
        playerList.add(socialPlayer);
    }

    public void registerSocialPlayer(final @NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getSettings().getChat().getDefaultChannel();
        ChatChannel defaultChatChannel = Social.get().getChatManager().getChannel(defaultChatChannelName);

        SocialUser socialPlayer = new SocialUser(uuid);
        socialPlayer.setMuted(false);
        socialPlayer.setSocialSpy(false);

        if (defaultChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultChatChannel.getMembers().contains(uuid))
                defaultChatChannel.addMember(uuid);

            socialPlayer.setMainChannel(defaultChatChannel);
        }

        registerSocialPlayer(socialPlayer);
    }

    public void unregisterSocialPlayer(final @NotNull SocialUser socialPlayer) {
        playerList.remove(socialPlayer);
    }

    public void setMainChannel(final @NotNull SocialUser socialPlayer,
                               final @NotNull ChatChannel chatChannel) {

        ChatChannel previousChannel = socialPlayer.getMainChannel();

        SocialChannelPreSwitchEvent socialChannelPreSwitchEvent = new SocialChannelPreSwitchEvent(socialPlayer, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPreSwitchEvent);

        if (socialChannelPreSwitchEvent.isCancelled())
            return;

        socialPlayer.setMainChannel(chatChannel);
        SocialChannelPostSwitchEvent socialChannelPostSwitchEvent = new SocialChannelPostSwitchEvent(socialPlayer, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPostSwitchEvent);
    }

    public void setLatestMessage(final @NotNull SocialUser socialPlayer,
                                 final long latestMessageInMilliseconds) {

        socialPlayer.setLatestMessageInMilliseconds(latestMessageInMilliseconds);
    }

    public void setSocialSpy(final @NotNull SocialUser socialPlayer,
                             final boolean socialSpy) {

        socialPlayer.setSocialSpy(socialSpy);
    }

}