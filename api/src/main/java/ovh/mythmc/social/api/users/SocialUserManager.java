package ovh.mythmc.social.api.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPreSwitchEvent;
import ovh.mythmc.social.api.events.users.SocialUserIgnoreEvent;
import ovh.mythmc.social.api.events.users.SocialUserMuteStatusChangeEvent;
import ovh.mythmc.social.api.events.users.SocialUserUnignoreEvent;
import ovh.mythmc.social.api.users.SocialUser.IgnoreScope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();
    private static final List<SocialUser> userList = new ArrayList<>();

    public @NotNull List<SocialUser> get() {
        return List.copyOf(userList);
    }

    public SocialUser get(final @NotNull UUID uuid) {
        for (SocialUser player : userList) {
            if (player.getUuid().equals(uuid))
                return player;
        }

        return null;
    }

    public void register(final @NotNull SocialUser user) {
        userList.add(user);
    }

    public void register(final @NotNull UUID uuid) {
        // Todo: recover data from last session
        String defaultChatChannelName = Social.get().getConfig().getSettings().getChat().getDefaultChannel();
        ChatChannel defaultChatChannel = Social.get().getChatManager().getDefaultChannel();

        SocialUser user = new SocialUser(uuid);
        user.setSocialSpy(false);

        if (defaultChatChannel == null) {
            Social.get().getLogger().warn("Default channel '" + defaultChatChannelName + "' is unavailable!");
        } else {
            if (!defaultChatChannel.getMembers().contains(uuid))
                defaultChatChannel.addMember(uuid);

            user.setMainChannel(defaultChatChannel);
        }

        register(user);
    }

    public void unregister(final @NotNull SocialUser user) {
        userList.remove(user);
    }

    public void setMainChannel(final @NotNull SocialUser user,
                               final @NotNull ChatChannel chatChannel) {

        ChatChannel previousChannel = user.getMainChannel();

        SocialChannelPreSwitchEvent socialChannelPreSwitchEvent = new SocialChannelPreSwitchEvent(user, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPreSwitchEvent);

        if (socialChannelPreSwitchEvent.isCancelled())
            return;

        user.setMainChannel(chatChannel);
        SocialChannelPostSwitchEvent socialChannelPostSwitchEvent = new SocialChannelPostSwitchEvent(user, previousChannel, chatChannel);
        Bukkit.getPluginManager().callEvent(socialChannelPostSwitchEvent);
    }

    public void setLatestMessage(final @NotNull SocialUser user,
                                 final long latestMessageInMilliseconds) {

        user.setLatestMessageInMilliseconds(latestMessageInMilliseconds);
    }

    public void setSocialSpy(final @NotNull SocialUser user,
                             final boolean socialSpy) {

        user.setSocialSpy(socialSpy);
    }

    public boolean isGloballyMuted(final @NotNull SocialUser user) {
        return user.getBlockedChannels().containsAll(Social.get().getChatManager().getChannels().stream().map(channel -> channel.getName()).toList());
    }

    public boolean isMuted(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        return user.getBlockedChannels().contains(channel.getName());
    }

    public void mute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        SocialUserMuteStatusChangeEvent event = new SocialUserMuteStatusChangeEvent(user, channel, true);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        user.getBlockedChannels().add(channel.getName());
    }

    public void unmute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        SocialUserMuteStatusChangeEvent event = new SocialUserMuteStatusChangeEvent(user, channel, false);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        user.getBlockedChannels().remove(channel.getName());
    }

    public boolean isIgnored(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        return user.getIgnoredUsers().containsKey(target.getUuid());
    }

    public IgnoreScope getIgnoreScope(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        return user.getIgnoredUsers().get(target.getUuid());
    } 

    public void ignore(final @NotNull SocialUser user, final @NotNull SocialUser target, final @NotNull IgnoreScope scope) {
        SocialUserIgnoreEvent socialUserIgnoreEvent = new SocialUserIgnoreEvent(user, target, scope);
        Bukkit.getPluginManager().callEvent(socialUserIgnoreEvent);

        if (socialUserIgnoreEvent.isCancelled())
            return;

        user.getIgnoredUsers().put(target.getUuid(), scope);
    }

    public void unignore(final @NotNull SocialUser user, final @NotNull SocialUser target) {
        SocialUserUnignoreEvent socialUserUnignoreEvent = new SocialUserUnignoreEvent(user, target);
        Bukkit.getPluginManager().callEvent(socialUserUnignoreEvent);

        if (socialUserUnignoreEvent.isCancelled())
            return;

        user.getIgnoredUsers().remove(target.getUuid());
    }

}
