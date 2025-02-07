package ovh.mythmc.social.api.users;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.database.SocialDatabase;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPreSwitchEvent;
import ovh.mythmc.social.api.events.users.SocialUserMuteStatusChangeEvent;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialUserManager {

    public static final SocialUserManager instance = new SocialUserManager();

    // Gets ONLINE users
    public @NotNull Collection<SocialUser> get() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> get(player.getUniqueId()))
            .filter(user -> user != null)
            .toList();
    }

    @Deprecated(forRemoval = true)
    public SocialUser get(final @NotNull UUID uuid) {
        return getByUuid(uuid);
    }

    public SocialUser getByUuid(final @NotNull UUID uuid) {
        SocialUser user = SocialDatabase.get().getUserByUuid(uuid);
        if (user != null && user.getMainChannel() == null)
            user.setMainChannel(Social.get().getChatManager().getDefaultChannel());
        
        return user;
    }

    public Collection<SocialUser> getSocialSpyUsers() {
        return get().stream()
            .filter(SocialUser::isSocialSpy)
            .toList();
    }

    public Collection<SocialUser> getSocialSpyUsersInChannel(ChatChannel channel) {
        return get().stream()
            .filter(user -> user.isSocialSpy() && user.getMainChannel().equals(channel))
            .toList();
    }

    public void register(final @NotNull SocialUser user) {
        SocialDatabase.get().create(user);
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
            if (!defaultChatChannel.getMemberUuids().contains(uuid))
                defaultChatChannel.addMember(uuid);

            user.setMainChannel(defaultChatChannel);
        }

        register(user);
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

        SocialDatabase.get().update(user);
    }

    public void setLatestMessage(final @NotNull SocialUser user,
                                 final long latestMessageInMilliseconds) {

        user.setLatestMessageInMilliseconds(latestMessageInMilliseconds);

        SocialDatabase.get().update(user);
    }

    public void setSocialSpy(final @NotNull SocialUser user,
                             final boolean socialSpy) {

        user.setSocialSpy(socialSpy);

        SocialDatabase.get().update(user);
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
        SocialDatabase.get().update(user);
    }

    public void unmute(final @NotNull SocialUser user, final @NotNull ChatChannel channel) {
        SocialUserMuteStatusChangeEvent event = new SocialUserMuteStatusChangeEvent(user, channel, false);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        user.getBlockedChannels().remove(channel.getName());
        SocialDatabase.get().update(user);
    }

    public void enableCompanion(final @NotNull SocialUser user) {
        user.setCompanion(new SocialUserCompanion(user));

        SocialDatabase.get().update(user);
    }

    public void disableCompanion(final @NotNull SocialUser user) {
        user.setCompanion(null);

        SocialDatabase.get().update(user);
    }

}
